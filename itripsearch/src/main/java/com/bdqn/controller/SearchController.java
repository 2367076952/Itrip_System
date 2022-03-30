package com.bdqn.controller;

import com.bdqn.common.Dto;
import com.bdqn.common.DtoUtil;
import com.bdqn.dao.BaseDao;
import com.bdqn.entity.ItripHotelVO;
import com.bdqn.entity.Page;
import com.bdqn.entity.SearchHotCityVO;
import com.bdqn.entity.SearchHotelVO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.util.Date;

@RequestMapping("/api")
@Controller
public class SearchController {
    @RequestMapping(value = "hotellist/searchItripHotelListByHotCity",produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public Dto getlist111(@RequestBody SearchHotCityVO searchHotCityVO) throws SolrServerException, IOException {
        BaseDao baseDao =new BaseDao();
        SolrQuery solrQuery =new SolrQuery("*:*");
        solrQuery.addFilterQuery("cityId:"+searchHotCityVO.getCityId());
        return DtoUtil.returnDataSuccess(baseDao.getlist(solrQuery,searchHotCityVO.getCount()));
    }

    @RequestMapping(value = "hotellist/searchItripHotelPage",produces = "application/json",method = RequestMethod.POST)
    @ResponseBody
    public Dto<Page<ItripHotelVO>> searchItripHotelPage(@RequestBody SearchHotelVO vo) throws SolrServerException, IOException {
        BaseDao baseDao =new BaseDao();
        SolrQuery solrQuery =new SolrQuery();
        solrQuery.setQuery("*:*");
        int pageIndex =1;

        if (vo.getHotelLevel()!=null){
            solrQuery.addFilterQuery("hotelLevel:"+vo.getHotelLevel());
        }
        if (vo.getDestination()!=null){
            solrQuery.addFilterQuery("destination:"+vo.getDestination());

        }
        if (vo.getKeywords()!=null){
            solrQuery.addFilterQuery("keyword:"+vo.getKeywords());

        }
        if (vo.getPageNo()!=null){
           pageIndex =vo.getPageNo();
        }
        Page obj = baseDao.getlist(solrQuery,pageIndex,5);
        return DtoUtil.returnDataSuccess(obj);
    }


}
