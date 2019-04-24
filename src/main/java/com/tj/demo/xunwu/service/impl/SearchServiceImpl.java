package com.tj.demo.xunwu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.tj.demo.xunwu.base.HouseSort;
import com.tj.demo.xunwu.base.RentValueBlock;
import com.tj.demo.xunwu.entity.House;
import com.tj.demo.xunwu.entity.HouseDetail;
import com.tj.demo.xunwu.entity.HouseTag;
import com.tj.demo.xunwu.form.RentSearch;
import com.tj.demo.xunwu.repository.HouseDetailRepository;
import com.tj.demo.xunwu.repository.HouseRepository;
import com.tj.demo.xunwu.repository.HouseTagRepository;
import com.tj.demo.xunwu.service.ISearchService;
import com.tj.demo.xunwu.service.ServiceMultResult;
import com.tj.demo.xunwu.service.ServiceResult;
import com.tj.demo.xunwu.service.search.HouseIndexKey;
import com.tj.demo.xunwu.service.search.HouseIndexMessage;
import com.tj.demo.xunwu.service.search.HouseIndexTemplate;
import com.tj.demo.xunwu.service.search.HouseSuggest;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class SearchServiceImpl  implements ISearchService{

    private static final String  INDEX_NAME="xunwu";

    private static final String INDEX_TYPE="house";

    private static final String INDEX_TOPIC="house_build";

    @Autowired
    private HouseRepository houseRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TransportClient esClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC )
    private void handleMessage(String content){
        try {
            HouseIndexMessage message=objectMapper.readValue(content, HouseIndexMessage.class);
            switch (message.getOperation()){
                case HouseIndexMessage.INDEX:
                    this.createOrUpdateIndex(message);
                    break;
                case HouseIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                    default:
                        log.warn("Not support message content :"+content);
                        break;
            }
        } catch (IOException e) {
            log.error(" Cannot parse json for "+content,e);
        }
    }

    private void removeIndex(HouseIndexMessage message) {
        Long houseId=message.getHouseId();
        DeleteByQueryRequestBuilder builder=DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId))
                .source(INDEX_NAME);
        log.debug(" Delete by query for house: "+houseId);

        BulkByScrollResponse response=builder.get();
        long deleted=response.getDeleted();
        log.debug(" Delete total :"+deleted);
        if(deleted<=0){
            this.remove(houseId,message.getRetry()+1);
        }
    }

    private void createOrUpdateIndex(HouseIndexMessage message){
        Long houseId=message.getHouseId();
        Optional<House> houseOptional
                =houseRepository.findById(Integer.valueOf(houseId+""));
        House house=houseOptional.get();
        if(house==null){
            //如果失败的花，消息将重入队列
            log.error("index house {} does not exist!",houseId);
            this.index(houseId,message.getRetry()+1);
            return ;
        }

        HouseIndexTemplate indexTemplate=new HouseIndexTemplate();
        HouseDetail detail=houseDetailRepository.findByHouseId(Integer.valueOf(houseId+""));
        if(detail==null){
            //TODO 异常情况
        }
        modelMapper.map(detail,indexTemplate);
        indexTemplate.setCityEnName(house.getCityEnName());
        indexTemplate.setArea(house.getArea());
        indexTemplate.setTitle(house.getTitle());
        indexTemplate.setPrice(house.getPrice());
        indexTemplate.setCreateTime(house.getCreateTime());
        indexTemplate.setLastUpdateTime(house.getLastUpdateTime());
        indexTemplate.setRegionEnName(house.getRegionEnName());
        indexTemplate.setStreet(house.getStreet());
        indexTemplate.setDistrict(house.getDistrict());
        List<HouseTag> tags=houseTagRepository.findAllByHouseId(Integer.valueOf(houseId+""));
        if(tags!=null && !tags.isEmpty()){
            List<String> tagStrings=new ArrayList<>();
            tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
            indexTemplate.setTags(tagStrings);
        }

        SearchRequestBuilder requestBuilder=this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId));
        log.debug(requestBuilder.toString());
        SearchResponse searchResponse=requestBuilder.get();
        boolean success;
        long totalHit=searchResponse.getHits().getTotalHits();
        if(totalHit==0){
            success=create(indexTemplate);
        }else if(totalHit==1){
            String esId=searchResponse.getHits().getAt(0).getId();
            success=update(esId,indexTemplate);
        }else{
            success=deleteAndCreate(totalHit,indexTemplate);
        }
        if(success){
            log.debug("Index success with house "+houseId);
        }
    }

    @Override
    public void index(Long houseId) {
        this.index(houseId,0);
    }

    public void index(Long houseId,int retry){
        if(retry>HouseIndexMessage.MAX_RETRY){
            log.error("Retry index times over 3 for house:"+houseId+" Please check it");
            return;
        }
        HouseIndexMessage message=new HouseIndexMessage(houseId,HouseIndexMessage.INDEX,retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC,objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
           log.error("Json encode error for "+message);
        }
    }

    private boolean create(HouseIndexTemplate indexTemplate) {

        if(!updateSuggest(indexTemplate)){
            return false;
        }

        try {
            IndexResponse indexResponse=this.esClient.prepareIndex(INDEX_NAME,INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            log.debug("Create index with house: "+indexTemplate.getHouseId());
            if(indexResponse.status()== RestStatus.CREATED){
                return true;
            }else{
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index house "+indexTemplate.getHouseId(),e);
            return false;
        }
    }

    private boolean update(String esId,HouseIndexTemplate indexTemplate){
        if(!updateSuggest(indexTemplate)){
            return false;
        };
        try {
            UpdateResponse indexResponse=this.esClient.prepareUpdate(INDEX_NAME,INDEX_TYPE,esId)
                    .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();
            log.debug("Update index with house: "+indexTemplate.getHouseId());
            if(indexResponse.status()== RestStatus.OK){
                return true;
            }else{
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to update house "+indexTemplate.getHouseId(),e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit,HouseIndexTemplate indexTemplate){
        DeleteByQueryRequestBuilder builder=DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,indexTemplate.getHouseId()))
                .source(INDEX_NAME);
        log.debug("Delete by query for house:"+builder);
        BulkByScrollResponse response=builder.get();
        long deleted=response.getDeleted();
        if(deleted!=totalHit){
            log.warn("Need delete{},but {} was deleted!",totalHit,deleted);
            return false;
        }else{
            return create(indexTemplate);
        }
    }

    @Override
    public void remove(Long houseId) {
        this.remove(houseId,0);
    }

    @Override
    public ServiceMultResult<Integer> query(RentSearch rentSearch) {
        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME,rentSearch.getCityEnName()));
        if(rentSearch.getRegionEnName() !=null && !"*".equals(rentSearch.getRegionEnName())){
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME,rentSearch.getRegionEnName()));
        }
        //面积
        RentValueBlock area=RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if(!RentValueBlock.ALL.equals(area)){
           RangeQueryBuilder rangeQueryBuilder= QueryBuilders.rangeQuery(HouseIndexKey.AREA);
            if(area.getMax()>0){
                rangeQueryBuilder.lte(area.getMax());
            }
            if(area.getMin()>0){
                rangeQueryBuilder.gte(area.getMin());
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //价格
        RentValueBlock price=RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if(!RentValueBlock.ALL.equals(price)){
            RangeQueryBuilder priceRangeQueryBuilder= QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
            if(price.getMax()>0){
                priceRangeQueryBuilder.lte(price.getMax());
            }
            if(price.getMin()>0){
                priceRangeQueryBuilder.gte(price.getMin());
            }
            boolQueryBuilder.filter(priceRangeQueryBuilder);
        }

        //方向
        if(rentSearch.getDirection() >0 ){
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.DIRECTION,rentSearch.getDirection()));
        }
        //租住方式
        if(rentSearch.getRentWay()>-1){
            boolQueryBuilder.filter(QueryBuilders.termQuery(HouseIndexKey.RENT_WAY,rentSearch.getRentWay()));
        }

        /**
         * 关键词的复合查询
         */
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
                HouseIndexKey.TITLE,
                HouseIndexKey.TRAFFIC,
                HouseIndexKey.DISTRICT,
                HouseIndexKey.ROUND_SERVICE,
                HouseIndexKey.SUBWAY_LINE_NAME,
                HouseIndexKey.SUBWAY_STATION_NAME
                ));

        SearchRequestBuilder requestBuilder= this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(boolQueryBuilder)
                .addSort(HouseSort.getSortKey(rentSearch.getOrderBy()), SortOrder.fromString(rentSearch.getOrderDirection()))
                .setFrom(rentSearch.getStart())
                .setSize(rentSearch.getSize());
        log.debug(requestBuilder.toString());
        List<Integer> houseIds=new ArrayList<>();
        SearchResponse response=requestBuilder.get();
        if(response.status()!=RestStatus.OK){
            log.warn("Search status is no ok for "+requestBuilder);
            return new ServiceMultResult<>(0,houseIds);
        }
        for(SearchHit hit:response.getHits()){
            houseIds.add((Integer) hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID));
        }
        return new ServiceMultResult<>(response.getHits().totalHits,houseIds);
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {
        //这里的completionSuggestion里面填写的值与house_index_with_suggest里面构建的索引类型是 "type":"completion"的字段名称
        CompletionSuggestionBuilder suggestionBuilde= SuggestBuilders.completionSuggestion("suggest")
        .prefix(prefix).size(5);
        SuggestBuilder suggestBuilder=new SuggestBuilder();
        suggestBuilder.addSuggestion("autocomplete",suggestionBuilde); //前面属性的名称随便起

        SearchRequestBuilder requestBuilder=this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .suggest(suggestBuilder);
        log.debug(requestBuilder.toString());
        SearchResponse response=requestBuilder.get();
        Suggest suggest=response.getSuggest();
        Suggest.Suggestion result=suggest.getSuggestion("autocomplete");
        int maxSuggest=0;
        Set<String> suggestSet=new HashSet<>();
        for (Object term:result.getEntries()
             ) {
            if(term instanceof CompletionSuggestion.Entry){
                CompletionSuggestion.Entry item= (CompletionSuggestion.Entry) term;
                if(item.getOptions().isEmpty()){
                    continue;
                }
                for (CompletionSuggestion.Entry.Option option: item.getOptions()
                     ) {
                    String tip=option.getText().string();
                         if(suggestSet.contains(tip)){
                            continue;
                         }
                         suggestSet.add(tip);
                         maxSuggest++;
                }
            }
            if(maxSuggest>5){
                break;
            }
        }
        List<String> suggestsLists=Lists.newArrayList(suggestSet.toArray(new String[]{}));
        return ServiceResult.of(suggestsLists);
    }

    private boolean updateSuggest(HouseIndexTemplate indexTemplate){
       AnalyzeRequestBuilder requestBuilder=new AnalyzeRequestBuilder(
                this.esClient, AnalyzeAction.INSTANCE,INDEX_NAME,
                indexTemplate.getTitle(),indexTemplate.getLayoutDesc(),
                indexTemplate.getRoundService(),
                indexTemplate.getDescription(),indexTemplate.getSubwayLineName(),
                indexTemplate.getSubwayStationName()
        );
      /*  String [] aa=new String[]{"aa","bb","cc"};
        AnalyzeRequestBuilder requestBuilder=new AnalyzeRequestBuilder(
                this.esClient, AnalyzeAction.INSTANCE,indexTemplate.getTitle(),aa
        );*/
        requestBuilder.setAnalyzer("ik_smart");//设置分词器
        AnalyzeResponse resposne=requestBuilder.get();//获取分词结果
        List<AnalyzeResponse.AnalyzeToken> tokens=resposne.getTokens();
        if(tokens == null ){
            log.warn("can not analyze token for house:"+indexTemplate.getHouseId());
            return false;
        }

        List<HouseSuggest> suggests=new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token:tokens
             ) {
            //排序数字类型
            if("<NUM>".equals(token.getType()) || token.getTerm().length()<2){
                continue;
            }
            HouseSuggest suggest=new HouseSuggest();
            suggest.setInput(token.getTerm());
            suggests.add(suggest);
        }
        //定制化数据(小区)自动补全
        HouseSuggest suggest=new HouseSuggest();
        suggest.setInput(indexTemplate.getDistrict());
        suggests.add(suggest);
        indexTemplate.setSuggest(suggests);

        return true;
    }

    private void remove(Long houseId,int retry){
        if(retry>HouseIndexMessage.MAX_RETRY){
            log.error("Retry remove times over 3 for house:"+houseId+" Please check it !");
            return;
        }
        HouseIndexMessage message=new HouseIndexMessage(houseId,HouseIndexMessage.REMOVE,retry);
        try {
            kafkaTemplate.send(INDEX_TOPIC,objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
           log.error("Cannot encode json for "+message,e);
        }
    }
}
