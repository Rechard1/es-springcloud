package com.jwell56.security.cloud.service.netstruct.service.serviceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.netstruct.controller.AreaController;
import com.jwell56.security.cloud.service.netstruct.controller.UnitController;
import com.jwell56.security.cloud.service.netstruct.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.service.IBigScreenSettingService;
import com.jwell56.security.cloud.service.netstruct.service.feign.OrderComponent;
import com.jwell56.security.cloud.service.netstruct.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.netstruct.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.netstruct.utils.StringIdsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BigScreenSettingServiceImpl extends ServiceImpl<BaseMapper<BigScreenSetting>, BigScreenSetting> implements IBigScreenSettingService{

	@Autowired
	private RestHighLevelClient esClient;
	
	@Autowired
	private RoleAreaComponent roleAreaComponent;
	
	@Autowired
	private RoleUnitComponent roleUnitComponent;
	
	@Autowired
	private AreaController areaService;
	
	@Autowired
	private UnitController unitService;
	
	@Autowired
	private OrderComponent orderComponent;

	@Override
	public Map<String, Object> detail(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList,
			String unitIdList) {
		Map resMap = new HashMap();
		List<Integer> roleAreaList = roleAreaComponent.areaList(userInfo.getRoleId(),userInfo.getEnterpriseId());
	    List<Integer> roleUnitList = roleUnitComponent.unitList(userInfo.getRoleId(),userInfo.getEnterpriseId());
	    List<Integer> areaLists = (List<Integer>) areaService.getChildrens(areaIdList).getData();
	    List<Integer> unitLists = (List<Integer>) unitService.getChildrens(unitIdList).getData();
	    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	    log.info("start:" + start.format(df) + " end:" + end.format(df) + " roleId: " + userInfo.getRoleId().toString() + "===" + StringIdsUtil.StringIds(areaLists) + "===" +StringIdsUtil.StringIds(unitLists));
	    Map orderMap = orderComponent.getAssetDetail(start, end, userInfo.getRoleId(),userInfo.getEnterpriseId(), StringIdsUtil.StringIds(areaLists), StringIdsUtil.StringIds(unitLists));
	    int ruqinjiance = ruqinjiance(start, end, roleAreaList, roleUnitList, areaLists, unitLists);
	    int fanghu = fanghu(start, end, roleAreaList, roleUnitList, areaLists, unitLists);
	    int yunweijiankong = yunweijiankong(start, end, roleAreaList, roleUnitList, areaLists, unitLists);
	    int rizhishenji = rizhishenji(start, end, roleAreaList, roleUnitList, areaLists, unitLists);
	    resMap.putAll(orderMap);
	    resMap.put("ruqinjiance", ruqinjiance == 0 ? "未接入" : ruqinjiance);
	    resMap.put("fanghu", fanghu == 0 ? "未接入" : fanghu);
	    resMap.put("yunweijiankong", yunweijiankong == 0 ? "未接入" : yunweijiankong);
	    resMap.put("rizhishenji", rizhishenji == 0 ? "未接入" : rizhishenji);
		return resMap;
	}
	
	private int ruqinjiance(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists) {
		int ruqinjiance = esType(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "intrusion_result");
	   return ruqinjiance;
	}
	
	private int fanghu(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists) {
		int fanghu1 = esType(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "netflow_result");
		int fanghu2 = esType(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "anti_virus_result");
		int fanghu3 = esType(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "ips_result");
		int fanghu4 = esType(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "net_protection_result");
		return fanghu1 + fanghu2 + fanghu3 + fanghu4;
	}
	
	private int rizhishenji(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists) {
		int rizhishenji1 = esType2(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "syslog_system_sec");
		int rizhishenji2 = esType2(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "syslog_network_sec");
		int rizhishenji3 = esType2(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "syslog_database_sec");
		int rizhishenji4 = esType2(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "syslog_changer_sec");
		return rizhishenji1 + rizhishenji2 + rizhishenji3 + rizhishenji4;
	}
	
	private int yunweijiankong(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists) {
		int yunweijiankong = esType1(start, end, roleAreaList, roleUnitList, areaLists, unitLists, "base_anomaly_device");
		return yunweijiankong;
	}
	
	private int esType1(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists,String flag) {
		 // 1、创建search请求
        SearchRequest searchRequest = new SearchRequest(flag);
//        final Scroll scroll = new Scroll(TimeValue.timeValueSeconds(60));
//        
//        searchRequest.scroll(scroll);
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
//        sourceBuilder.size(20);
        //搜索条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
        if(!roleAreaList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("area_id",roleAreaList);
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return 0;
        }
        if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("area_id",areaLists);
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("unit_id",roleUnitList);
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return 0;
        }
        if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("unit_id",unitLists);
            boolQueryBuilder.must(queryBuilder2);
        }
//        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("rowkey").field("rowkey.keyword");
//        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
//        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.query(boolQueryBuilder);
        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        try {
        //3、发送请求
        SearchResponse searchResponse = esClient.search(searchRequest,RequestOptions.DEFAULT);
        //处理搜索命中文档结果
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits().value;
        
        return (int) totalHits;
        } catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private int esType(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists,String flag) {
		 // 1、创建search请求
       SearchRequest searchRequest = new SearchRequest(flag);
//       searchRequest.scroll(scroll);
       // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
       SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
//       sourceBuilder.size(20);
       //搜索条件
       BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
       QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
       		.from(TimeUtil.format(start))
       		.to(TimeUtil.format(end));
       boolQueryBuilder.must(queryBuilder);
       
       if(!areaLists.isEmpty()){
           QueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
           		.should(QueryBuilders.termsQuery("dvcaid",areaLists))
                   .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                   .should(QueryBuilders.termsQuery("d_area_id",areaLists));
           boolQueryBuilder.must(queryBuilder3);
       }
       if(!roleAreaList.isEmpty()){
           QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
           		.should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                   .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                   .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
           boolQueryBuilder.must(queryBuilder2);
       }else {
       	return 0;
       }
       if(!unitLists.isEmpty()){
           QueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
           		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                   .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                   .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
           boolQueryBuilder.must(queryBuilder3);
       }
       if(!roleUnitList.isEmpty()){
           QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
           		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                   .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                   .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
           boolQueryBuilder.must(queryBuilder2);
       }else {
       	return 0;
       }
//       TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("rowkey").field("rowkey.keyword");
//       sourceBuilder.aggregation(aggregationBuilder);//聚合查询
//       sourceBuilder.query(boolQueryBuilder);
       sourceBuilder.query(boolQueryBuilder);
       //将请求体加入到请求中
       searchRequest.source(sourceBuilder);
       try {
       //3、发送请求
       SearchResponse searchResponse = esClient.search(searchRequest,RequestOptions.DEFAULT);
       //处理搜索命中文档结果
       SearchHits hits = searchResponse.getHits();
       
       return (int) hits.getTotalHits().value;
       } catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private int esType2(LocalDateTime start, LocalDateTime end, List<Integer> roleAreaList, List<Integer> roleUnitList, List<Integer> areaLists, List<Integer> unitLists,String flag) {
		 // 1、创建search请求
      SearchRequest searchRequest = new SearchRequest(flag);
//      searchRequest.scroll(scroll);
      // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
      SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
//      sourceBuilder.size(20);
      //搜索条件
      BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
      QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
      		.from(TimeUtil.format(start))
      		.to(TimeUtil.format(end));
      boolQueryBuilder.must(queryBuilder);
      
      if(!roleAreaList.isEmpty()){
        	 QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("asset_area_id",roleAreaList);
             boolQueryBuilder.must(queryBuilder2);
         }else {
        	 return 0;
         }
         if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("asset_area_id",areaLists);
            boolQueryBuilder.must(queryBuilder2);
         }
         if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("asset_unit_id",roleUnitList);
            boolQueryBuilder.must(queryBuilder2);
         }else {
        	 return 0;
         }
         if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.termsQuery("asset_unit_id",unitLists);
            boolQueryBuilder.must(queryBuilder2);
         }
//      TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("rowkey").field("rowkey.keyword");
//      sourceBuilder.aggregation(aggregationBuilder);//聚合查询
//      sourceBuilder.query(boolQueryBuilder);
      sourceBuilder.query(boolQueryBuilder);
      //将请求体加入到请求中
      searchRequest.source(sourceBuilder);
      try {
      //3、发送请求
      SearchResponse searchResponse = esClient.search(searchRequest,RequestOptions.DEFAULT);
      //处理搜索命中文档结果
      SearchHits hits = searchResponse.getHits();
      
      return (int) hits.getTotalHits().value;
      } catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
