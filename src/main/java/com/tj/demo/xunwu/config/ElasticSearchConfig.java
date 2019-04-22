package com.tj.demo.xunwu.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class ElasticSearchConfig {

    @Bean
    public TransportClient esClient() throws UnknownHostException {
        Settings settings=Settings.builder()
                .put("cluster.name","elk1")
                .put("client.transport.sniff","true")
                .build();

        TransportAddress master=new TransportAddress(InetAddress.getByName("192.168.213.132"),9300);
        TransportClient client=null;
        try{
            client=new PreBuiltTransportClient(settings)
                    .addTransportAddress(master);
        }catch (Exception e){
            e.printStackTrace();;
        }
        return client;
    }
}
