package com.tj.demo.xunwu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tj.demo.xunwu.entity.House;
import com.tj.demo.xunwu.entity.HouseDetail;
import com.tj.demo.xunwu.entity.HouseTag;
import com.tj.demo.xunwu.repository.HouseDetailRepository;
import com.tj.demo.xunwu.repository.HouseRepository;
import com.tj.demo.xunwu.repository.HouseTagRepository;
import com.tj.demo.xunwu.service.ISearchService;
import com.tj.demo.xunwu.service.search.HouseIndexKey;
import com.tj.demo.xunwu.service.search.HouseIndexTemplate;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SearchServiceImpl  implements ISearchService{

    private static final String  INDEX_NAME="xunwu";

    private static final String INDEX_TYPE="house";

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


    @Override
    public boolean index(Long houseId) {
        Optional<House> houseOptional
                =houseRepository.findById(Integer.valueOf(houseId+""));
        House house=houseOptional.get();
        if(house==null){
            log.error("index house {} does not exist!",houseId);
        }

        HouseIndexTemplate indexTemplate=new HouseIndexTemplate();
        HouseDetail detail=houseDetailRepository.findByHouseId(Integer.valueOf(houseId+""));
        if(detail==null){
            //TODO 异常情况
            return false;
        }
        modelMapper.map(detail,indexTemplate);

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
        return success;
    }

    private boolean create(HouseIndexTemplate indexTemplate) {
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
    public boolean remove(Long houseId) {
        DeleteByQueryRequestBuilder builder=DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId))
                .source(INDEX_NAME);
        log.debug(" Delete by query for house: "+houseId);

        BulkByScrollResponse response=builder.get();
        long deleted=response.getDeleted();
        log.debug(" Delete total :"+deleted);
        return true;
    }
}
