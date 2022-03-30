package com.bdqn.dao;


import com.bdqn.entity.ItripHotelVO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.io.IOException;
import java.util.List;

public class MainTest {
    static String url ="http://localhost:8080/solr-4.9.1/hotelCore";

    public static void main(String[] args) throws SolrServerException, IOException {
        HttpSolrClient solrClient =new HttpSolrClient(url);
        solrClient.setParser(new XMLResponseParser());
        solrClient.setConnectionTimeout(1000);
        SolrQuery solrQuery =new SolrQuery();
        solrQuery.setQuery("*:*");
        solrQuery.addFilterQuery("keyword:北京");
        solrQuery.setSort("id",SolrQuery.ORDER.desc);
        SolrResponse response =solrClient.query(solrQuery);
        List<ItripHotelVO> list = ((QueryResponse)response).getBeans(ItripHotelVO.class);
        list.forEach(System.out::println);
    }
}
