package com.bdqn.dao;

import com.bdqn.common.Dto;
import com.bdqn.entity.ItripHotelVO;
import com.bdqn.entity.Page;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

public class BaseDao {

    static String url = "http://localhost:8080/solr-4.9.1/hotelCore";
    HttpSolrClient solrClient;

    public BaseDao() {

        solrClient = new HttpSolrClient(url);
        solrClient.setParser(new XMLResponseParser());
        solrClient.setConnectionTimeout(1000);
    }

    public List<ItripHotelVO> getlist(SolrQuery solrQuery, int count) throws SolrServerException, IOException {
        solrQuery.setStart(0);
        solrQuery.setRows(count);
        SolrResponse response = solrClient.query(solrQuery);
        List<ItripHotelVO> list = ((QueryResponse) response).getBeans(ItripHotelVO.class);
        return list;
    }
//    根据条件分页搜索方法
    public Page<ItripHotelVO> getlist(@RequestBody SolrQuery solrQuery,int pageIndex,int pageSize) throws SolrServerException, IOException {
    solrQuery.setStart((pageIndex-1)*pageSize);
    solrQuery.setRows(pageSize);
    SolrResponse response =solrClient.query(solrQuery);
    List<ItripHotelVO> list =((QueryResponse)response).getBeans(ItripHotelVO.class);
        SolrDocumentList solrDocuments = ((QueryResponse)response).getResults();
        Page<ItripHotelVO> page =new Page<>(pageIndex,pageSize,new Long(solrDocuments.getNumFound()).intValue());
        page.setRows(list);
        return  page;

    }

}
