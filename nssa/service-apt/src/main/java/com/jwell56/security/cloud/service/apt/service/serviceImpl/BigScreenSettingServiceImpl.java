package com.jwell56.security.cloud.service.apt.service.serviceImpl;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.apt.entity.Apt;
import com.jwell56.security.cloud.service.apt.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.apt.entity.User;
import com.jwell56.security.cloud.service.apt.service.IBigScreenSettingService;
import com.jwell56.security.cloud.service.apt.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.apt.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.apt.service.feign.RoleUnitComponent;

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
	private NetStructComponent netStructComponent;
	
	@Override
	public Map<String, Object> getZongHeGrade(LocalDateTime start,LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList) {
		Map<String, Object> resMap = new HashMap<>();
		// 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource("grade",null).collapse();
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        
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
        	return resMap;
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
        	return resMap;
        }
        if(flag.equals("JingWai")) {
        	List<String> paramList = new ArrayList<String>();
        	paramList.add("CN");
        	paramList.add("");
        	paramList.add("B1");
        	QueryBuilder queryBuilder3 =QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("scode.keyword",paramList))
                    .must(QueryBuilders.termsQuery("dcode.keyword",paramList));
            boolQueryBuilder.mustNot(queryBuilder3);
        }
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("grade").field("grade.keyword");
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.size(100);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        try {
			SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get("grade");
            Terms teamSum= (Terms)tags;
            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
            for (Terms.Bucket bucket : buckets) {
//                list.add(bucket.getKeyAsString());
                resMap.put(bucket.getKeyAsString(), bucket.getDocCount());
            }		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(resMap.isEmpty()) {
        	resMap.put("高",0L);
        	resMap.put("中",0L);
        	resMap.put("低",0L);
        	resMap.put("total",0L);
        	return resMap;
        }
        if(!resMap.containsKey("高")) {
        	resMap.put("高",0L);
        }
        if(!resMap.containsKey("中")) {
        	resMap.put("中",0L);
        }
        if(!resMap.containsKey("低")) {
        	resMap.put("低",0L);
        }
        long total = 0;
		for(Map.Entry<String, Object> entry : resMap.entrySet()) {
			total += Long.parseLong(entry.getValue().toString());
		}
		resMap.put("total", total);
		return resMap;
	}

	@Override
	public Map<String, Object> getZongHeDevice(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList) {
		Map<String, Object> resMap = new LinkedHashMap<>();
		// 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().size(2000);
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource("sip",null).collapse();
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
//        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("enterprise_id",userInfo.getEnterpriseId());
//        boolQueryBuilder.must(queryBuilder1);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        if(!roleAreaList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                    .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                    .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",areaLists))
                    .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                    .should(QueryBuilders.termsQuery("d_area_id",areaLists));
            boolQueryBuilder.must(queryBuilder3);
        }
        if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                    .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                    .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                    .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                    .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
            boolQueryBuilder.must(queryBuilder3);
        }
        if(flag.equals("JingWai")) {
        	List<String> paramList = new ArrayList<String>();
        	paramList.add("CN");
        	paramList.add("");
        	paramList.add("B1");
        	QueryBuilder queryBuilder3 =QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("scode.keyword",paramList))
                    .must(QueryBuilders.termsQuery("dcode.keyword",paramList));
            boolQueryBuilder.mustNot(queryBuilder3);
        }
        
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("sip").field("sip.keyword");
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.size(100);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        JSONArray countArray = new JSONArray();
        try {
			SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
//            Aggregations aggregations = searchResponse.getAggregations();
            
            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
			JSONObject aggregationsJson1 = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("hits");
			countArray = aggregationsJson.getJSONObject("sterms#sip").getJSONArray("buckets");
            
			List<Map<String, Object>> aptList = new ArrayList<>();
			countArray.forEach(groupCount -> {
				Map<String, Object> map = new HashMap<>();
	            JSONObject groupCountJson = (JSONObject) groupCount;
	            JSONArray countArray1 = aggregationsJson1.getJSONArray("hits");
	            countArray1.forEach(groupCount1 -> {
	            	JSONObject groupCountJson1 = (JSONObject) groupCount1;
	            	if(groupCountJson.getString("key").equals(groupCountJson1.getJSONObject("_source").getString("sip"))) {
	            		map.put("s_name", groupCountJson1.getJSONObject("_source").getString("s_name"));
	            	}
	            });
	            map.put("ip", groupCountJson.getString("key"));
	            map.put("counts", groupCountJson.getString("doc_count"));
	            aptList.add(map);
	        });
			 Collections.sort(aptList, new Comparator<Map<String, Object>>()
		        {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						 int compare = Integer.parseInt(o1.get("counts").toString()) - Integer.parseInt(o2.get("counts").toString());
			             return -compare;
					}
		        });
			List<Map<String, Object>> aptList1 = aptList.size() < 5 ? aptList : aptList.subList(0, 5);
			for(Map<String, Object> map : aptList1) {
				if(!map.containsKey("s_name") || StringUtils.isEmpty(map.get("s_name").toString())) {
					resMap.put(map.get("ip").toString(), map.get("counts"));
				}else {
					resMap.put(map.get("s_name").toString(), map.get("counts"));
				}
			}
            
//            Aggregation tags = aggregations.asMap().get("sip");
//            Terms teamSum= (Terms)tags;
//            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
//            for (Terms.Bucket bucket : buckets) {
////                list.add(bucket.getKeyAsString());
//                resMap.put(bucket.getKeyAsString(), bucket.getDocCount());
//            }		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}
	
	//排序加截取前5个
//	private Map<String, Object> sort(Map<String, Object> paramMap){
//		Map<String, Object> sortMap = new LinkedHashMap<String, Object>();
//		if(paramMap == null || paramMap.isEmpty()) return sortMap;
//		
//		List<Map.Entry<String, Object>> list = new LinkedList<Map.Entry<String, Object>>(paramMap.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<String, Object>>()
//        {
//            @Override
//            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2)
//            {
//                int compare = Integer.parseInt(o1.getValue().toString()) - Integer.parseInt(o2.getValue().toString());
//                return -compare;
//            }
//        });
// 
//        List<Map.Entry<String, Object>> newList = list.size() < 5 ? list : list.subList(0, 5);
//        for (Map.Entry<String, Object> entry : newList) {
//        	sortMap.put(entry.getKey(), entry.getValue());
//        }
//		return sortMap;
//	}

	@Override
	public Map<String, Object> getZongHeType(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList) {
		Map<String, Object> resMap = new HashMap<>();
		// 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource("types",null).collapse();
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
//        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("enterprise_id",userInfo.getEnterpriseId());
//        boolQueryBuilder.must(queryBuilder1);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        if(!roleAreaList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                    .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                    .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",areaLists))
                    .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                    .should(QueryBuilders.termsQuery("d_area_id",areaLists));
            boolQueryBuilder.must(queryBuilder3);
        }
        if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                    .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                    .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                    .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                    .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
            boolQueryBuilder.must(queryBuilder3);
        }
        if(flag.equals("JingWai")) {
        	List<String> paramList = new ArrayList<String>();
        	paramList.add("");
        	paramList.add("CN");
        	paramList.add("B1");
        	QueryBuilder queryBuilder3 =QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("scode.keyword",paramList))
                    .must(QueryBuilders.termsQuery("dcode.keyword",paramList));
            boolQueryBuilder.mustNot(queryBuilder3);
        }
        
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("types").field("types.keyword");
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.size(100);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        try {
			SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get("types");
            Terms teamSum= (Terms)tags;
            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
            for (Terms.Bucket bucket : buckets) {
//                list.add(bucket.getKeyAsString());
                resMap.put(bucket.getKeyAsString(), bucket.getDocCount());
            }		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return typeMap(resMap);
	}

	@Override
	public List<Apt> detail(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList) throws IOException, ParseException {
        // 1、创建search请求
		List<Apt> resList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
//        searchRequest.scroll(scroll);
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
        sourceBuilder.size(20);
        sourceBuilder.sort("happen_time", SortOrder.DESC);
        //搜索条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        if(!roleAreaList.isEmpty()){
        	  QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
              		  .should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                      .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                      .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
              boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resList;
        }
        if(!areaLists.isEmpty()){
      	  QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		  .should(QueryBuilders.termsQuery("dvcaid",areaLists))
                    .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                    .should(QueryBuilders.termsQuery("d_area_id",areaLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!roleUnitList.isEmpty()){
        	  QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
              		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                      .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                      .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
              boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resList;
        }
        if(!unitLists.isEmpty()){
      	  QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                    .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                    .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
            boolQueryBuilder.must(queryBuilder2);
      }
        sourceBuilder.query(boolQueryBuilder);
        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);

        //3、发送请求
        SearchResponse searchResponse = esClient.search(searchRequest,RequestOptions.DEFAULT);
        //处理搜索命中文档结果
        SearchHits hits = searchResponse.getHits();
//        long totalHits = hits.getTotalHits().value;

        while (searchResponse.getHits().getHits().length != 0){
        	for (SearchHit hit : hits.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象
                setDataApt(sourceAsMap,resList);
            }
            log.info("ES分页查询apt成功");
            break;
        }
        return resList;
	}
	
	private void setDataApt(Map<String, Object> map ,List<Apt> list) throws ParseException {
		 DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		 Apt apt = new Apt();
		 if(map.get("dvcid")!=null){
			 apt.setDeviceid(Integer.parseInt(map.get("dvcid").toString()));
	     }
	     if(map.get("happen_time")!=null){
	    	apt.setHappenTime(LocalDateTime.parse((String) map.get("happen_time"), df));
	     }
	     if(map.get("sip")!=null){
	    	 apt.setSip((String) map.get("sip"));
		 }
	     if(map.get("dip")!=null){
	    	 apt.setDip((String) map.get("dip"));
		 }
//	     if(map.get("create_time")!=null){
//		     apt.setCreateTime(LocalDateTime.ofEpochSecond(Long.parseLong(map.get("create_time").toString()),0, ZoneOffset.ofHours(8)));
//		 }
	     if(map.get("types")!=null){
		     apt.setRiskType((String) map.get("types"));
		 }
	     if(map.get("grade")!=null){
		     apt.setRiskGrade((String) map.get("grade"));
		 }
	     if(map.get("dvcaid")!=null){
	         apt.setDeviceAreaName(netStructComponent.getAreaName(Integer.parseInt(map.get("dvcaid").toString())));
	     }
	     if(map.get("dvcuid")!=null){
	         apt.setDeviceUnitName(netStructComponent.getUnitName(Integer.parseInt(map.get("dvcuid").toString())));
	     }
	     list.add(apt);
	 }

	public Map<String, Object> getByIp(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList) {
		Map<String, Object> resMap = new HashMap<>();
		// 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource(flag,null).collapse();
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        if(!roleAreaList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                    .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                    .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",areaLists))
                    .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                    .should(QueryBuilders.termsQuery("d_area_id",areaLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                    .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                    .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                    .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                    .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(flag).field(flag+".keyword").size(500000);
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.size(100);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        try {
			SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get(flag);
            Terms teamSum= (Terms)tags;
            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
//            for (Terms.Bucket bucket : buckets) {
////              list.add(bucket.getKeyAsString());
//              resMap.put(bucket.getKeyAsString(), bucket.getDocCount());
//          }
            resMap.put(flag, buckets.size());
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resMap;
	}
	
	@Override
	public Map<String, Object> getZhuDongIp(LocalDateTime start, LocalDateTime end, User userInfo, Integer timeType, String areaIdList, String unitIdList) {
		NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);
		Map<String, Object> resMap = new HashMap<>();
		Map<String, Object> sIp = getByIp(start, end, userInfo, "sip", areaIdList, unitIdList);
		Map<String, Object> dIp = getByIp(start, end, userInfo, "dip", areaIdList, unitIdList);
		int total = totalRows(start, end, userInfo, areaIdList, unitIdList);
		Map<String, LocalDateTime> timeMap = TimeUtil.getTimeMap(timeType, start, end);
		if(timeMap.containsKey("lastStart") && timeMap.containsKey("lastEnd")) {
			LocalDateTime lastStart = timeMap.get("lastStart");
			LocalDateTime lastEnd = timeMap.get("lastEnd");
//			Map<String, Object> rowkey1 = totalRows(lastStart, lastEnd, userInfo, areaIdList, unitIdList);
//			long total = rowkey.containsKey("rowkey") ? Long.parseLong(rowkey.get("rowkey").toString()) : 0;
			int lastTotal = totalRows(lastStart, lastEnd, userInfo, areaIdList, unitIdList);
			if(lastTotal == 0){
				resMap.put("flag", true);
				resMap.put("同比", "--");
			}else if(total - lastTotal < 0) {
				resMap.put("flag", false);
				resMap.put("同比", nf.format(((lastTotal - total)  * 100 / lastTotal)));
			}else {
				resMap.put("flag", true);
				resMap.put("同比", nf.format(((total - lastTotal) * 100 / lastTotal)));
			}
		}
		resMap.putAll(sIp);
		resMap.putAll(dIp);
		resMap.put("rowkey", total);
		return resMap;
	}

	@Override
	public Map<String, Object> getZhuDong(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList) {
		Map<String, Object> resMap = new HashMap<>();
		// 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource("sip",null).collapse();
//        sourceBuilder.fetchSource(flag,null).collapse();
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        if(!roleAreaList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                    .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                    .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",areaLists))
                    .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                    .should(QueryBuilders.termsQuery("d_area_id",areaLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                    .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                    .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                    .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                    .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(flag).field(flag+".keyword")
        		                     .subAggregation(AggregationBuilders.terms("sip").field("sip.keyword").size(1000000)).size(500000);
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.size(100);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        try {
			SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get(flag);
            Terms teamSum= (Terms)tags;
            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
            for (Terms.Bucket bucket : buckets) {
            	Aggregations aggregationsdd = bucket.getAggregations();
            	Aggregation tagsdd = aggregationsdd.asMap().get("sip");
            	Terms teamSumdd= (Terms)tagsdd;
                List<? extends Terms.Bucket> bucketsdd = teamSumdd.getBuckets();
//                for (Terms.Bucket bucketdd : bucketsdd) {
//                  System.out.println("types:"+bucket.getKey().toString() + "dip:"+bucket.getKey().toString()+  "sip:"+bucketdd.getKey().toString()+"次数："+bucketdd.getDocCount());
//                }
//              Aggregation tagsdd = aggregationsdd.asMap().get("sip");
//                list.add(bucket.getKeyAsString());
                resMap.put(bucket.getKeyAsString(), bucketsdd.size());
            }		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        if(flag.equals("sip")) {
//        	return sort(resMap);
//        }
        if(resMap.isEmpty()) {
        	resMap.put("高",0L);
        	resMap.put("中",0L);
        	resMap.put("低",0L);
        	resMap.put("total",0L);
        	return resMap;
        }
        if(!resMap.containsKey("高")) {
        	resMap.put("高",0L);
        }
        if(!resMap.containsKey("中")) {
        	resMap.put("中",0L);
        }
        if(!resMap.containsKey("低")) {
        	resMap.put("低",0L);
        }
        long total = 0;
		for(Map.Entry<String, Object> entry : resMap.entrySet()) {
			total += Long.parseLong(entry.getValue().toString());
		}
		resMap.put("total", total);
		return resMap;
	}
	
	@Override
	public Map<String, Object> getZhuDongRanking(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList) {
		Map<String, Object> resMap = new LinkedHashMap<>();
		// 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource("sip",null).fetchSource("s_name",null).size(5000);
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder  = QueryBuilders.rangeQuery("happen_time")
        		.from(TimeUtil.format(start))
        		.to(TimeUtil.format(end));
        boolQueryBuilder.must(queryBuilder);
        
        List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
        List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
        if(!roleAreaList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",roleAreaList))
                    .should(QueryBuilders.termsQuery("s_area_id",roleAreaList))
                    .should(QueryBuilders.termsQuery("d_area_id",roleAreaList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        if(!areaLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcaid",areaLists))
                    .should(QueryBuilders.termsQuery("s_area_id",areaLists))
                    .should(QueryBuilders.termsQuery("d_area_id",areaLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!unitLists.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",unitLists))
                    .should(QueryBuilders.termsQuery("s_unit_id",unitLists))
                    .should(QueryBuilders.termsQuery("d_unit_id",unitLists));
            boolQueryBuilder.must(queryBuilder2);
        }
        if(!roleUnitList.isEmpty()){
            QueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
            		.should(QueryBuilders.termsQuery("dvcuid",roleUnitList))
                    .should(QueryBuilders.termsQuery("s_unit_id",roleUnitList))
                    .should(QueryBuilders.termsQuery("d_unit_id",roleUnitList));
            boolQueryBuilder.must(queryBuilder2);
        }else {
        	return resMap;
        }
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("sip").field("sip.keyword").size(5000);
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);
//        sourceBuilder.size(100);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        JSONArray countArray = new JSONArray();
        try {
			SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
			JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
			JSONObject aggregationsJson1 = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("hits");
			countArray = aggregationsJson.getJSONObject("sterms#sip").getJSONArray("buckets");
            
			List<Map<String, Object>> aptList = new ArrayList<>();
			countArray.forEach(groupCount -> {
				Map<String, Object> map = new HashMap<>();
	            JSONObject groupCountJson = (JSONObject) groupCount;
	            JSONArray countArray1 = aggregationsJson1.getJSONArray("hits");
	            countArray1.forEach(groupCount1 -> {
	            	JSONObject groupCountJson1 = (JSONObject) groupCount1;
	            	if(groupCountJson.getString("key").equals(groupCountJson1.getJSONObject("_source").getString("sip"))) {
	            		map.put("s_name", groupCountJson1.getJSONObject("_source").getString("s_name"));
	            	}
	            });
	            map.put("ip", groupCountJson.getString("key"));
	            map.put("counts", groupCountJson.getString("doc_count"));
	            aptList.add(map);
	        });
			 Collections.sort(aptList, new Comparator<Map<String, Object>>()
		        {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						 int compare = Integer.parseInt(o1.get("counts").toString()) - Integer.parseInt(o2.get("counts").toString());
			             return -compare;
					}
		        });
			List<Map<String, Object>> aptList1 = aptList.size() < 5 ? aptList : aptList.subList(0, 5);
			for(Map<String, Object> map : aptList1) {
				if(!map.containsKey("s_name") || StringUtils.isEmpty(map.get("s_name").toString())) {
					resMap.put(map.get("ip").toString(), map.get("counts"));
				}else {
					resMap.put(map.get("s_name").toString(), map.get("counts"));
				}
			}
//			Aggregations aggregations = searchResponse.getAggregations();
//            Aggregation tags = aggregations.asMap().get("sip");
//            Terms teamSum= (Terms)tags;
//            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
//            for (Terms.Bucket bucket : buckets) {
//            	resMap.put(bucket.getKeyAsString(), bucket.getDocCount());
//            }		
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return resMap;
	}
	
	private Map<String, Object> typeMap(Map<String, Object> typeMap){
		Map<String, Object> newTypeMap = new LinkedHashMap<String, Object>();
		if(typeMap.size() > 6) {
			List<Map.Entry<String, Object>> list = new LinkedList<Map.Entry<String, Object>>(typeMap.entrySet());
	        Collections.sort(list, new Comparator<Map.Entry<String, Object>>()
	        {
	            @Override
	            public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2)
	            {
	                int compare = Integer.parseInt(o1.getValue().toString()) - Integer.parseInt(o2.getValue().toString());
	                return -compare;
	            }
	        });
	        int total = 0;
	        for(int i = 0; i < list.size() ; i++) {
	        	if(i > 4) {
	        		total += Integer.parseInt(list.get(i).getValue().toString());
	        	}else {
	        		newTypeMap.put(list.get(i).getKey(), list.get(i).getValue());
	        	}
	        }
	        newTypeMap.put("其它", total);
	        return newTypeMap;
		}
		return typeMap;
	}
	
	@Override
	public int totalRows(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList) {
		 // 1、创建search请求
      SearchRequest searchRequest = new SearchRequest("intrusion_result");
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
      
      List<Integer> roleAreaList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
      List<Integer> roleUnitList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
      List<Integer> areaLists = netStructComponent.getAreaChildrens(areaIdList);
      List<Integer> unitLists = netStructComponent.getUnitChildrens(unitIdList);
      
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
