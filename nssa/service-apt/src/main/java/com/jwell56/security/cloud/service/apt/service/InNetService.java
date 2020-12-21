package com.jwell56.security.cloud.service.apt.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.service.apt.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.apt.utils.IPUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * author Richard
 */

@Service
public class InNetService {


    @Autowired
    NetStructComponent netStructComponent;

    /**
     * sip限制为内网
     */
    public QueryBuilder queryWrapperForSipIn() {

        String sql = "";

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<Map<String, Object>> resultObject = netStructComponent.listInNet();
        if (resultObject != null) {
            for (Map map : resultObject) {
                if (map.get("startIp") != null && !((String) map.get("startIp")).isEmpty() &&
                        map.get("endIp") != null && !((String) map.get("endIp")).isEmpty()) {
//                    boolQueryBuilder.should(QueryBuilders.rangeQuery("sipNum").gte(map.get("startipNum")).lte(map.get("endipNum")));
//                    System.err.println(map.get("startIp").toString());
//                    System.err.println(map.get("endIp").toString());

                    System.err.println("minIp:" + IPUtil.ipToLong(map.get("startIp").toString()));
                    System.err.println("maxIp:" + IPUtil.ipToLong(map.get("endIp").toString()));

                    boolQueryBuilder.should(QueryBuilders.rangeQuery("sipNum")
                            .gte(IPUtil.ipToLong(map.get("startIp").toString()))
                            .lte(IPUtil.ipToLong(map.get("endIp").toString())));
                }
            }
        }
        return boolQueryBuilder;
    }


    /**
     * dip限制为内网
     */
    public QueryBuilder queryWrapperForDipIn() {

        String sql = "";

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<Map<String, Object>> resultObject = netStructComponent.listInNet();
        if (resultObject != null) {
            for (Map map : resultObject) {
                if (map.get("startIp") != null && !((String) map.get("startIp")).isEmpty() &&
                        map.get("endIp") != null && !((String) map.get("endIp")).isEmpty()) {
//                    boolQueryBuilder.should(QueryBuilders.rangeQuery("sipNum").gte(map.get("startipNum")).lte(map.get("endipNum")));

                    boolQueryBuilder.should(QueryBuilders.rangeQuery("dipNum")
                            .gte(IPUtil.ipToLong(map.get("startIp").toString()))
                            .lte(IPUtil.ipToLong(map.get("endIp").toString())));
                }
            }
        }
        return boolQueryBuilder;
    }



    /**
     * sip限制为外网
     */
    public QueryBuilder queryWrapperForSipOut() {

        String sql = "";

//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        List<Map<String, Long>> resultObject = netStructComponent.listOutNet();
//        if (resultObject != null) {
//            for (Map map : resultObject) {
//                if (map.get("startIp") != null && !((String) map.get("startIp")).isEmpty() &&
//                        map.get("endIp") != null && !((String) map.get("endIp")).isEmpty()) {
//                    boolQueryBuilder.should(QueryBuilders.rangeQuery("sipNum").gte(map.get("startIpNum")).lte(map.get("endIpNum")));
//
//                }
//            }
//        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<Map<String, Object>> resultObject = netStructComponent.listInNet();
        if (resultObject != null) {
            for (Map map : resultObject) {
                if (map.get("startIp") != null && !((String) map.get("startIp")).isEmpty() &&
                        map.get("endIp") != null && !((String) map.get("endIp")).isEmpty()) {
//                    boolQueryBuilder.should(QueryBuilders.rangeQuery("sipNum").gte(map.get("startipNum")).lte(map.get("endipNum")));

                    boolQueryBuilder.should(QueryBuilders.rangeQuery("dipNum")
                            .lte(IPUtil.ipToLong(map.get("startIp").toString()))
                            .gte(IPUtil.ipToLong(map.get("endIp").toString())));
                }
            }
        }
        System.err.println(boolQueryBuilder.getWriteableName());
        return boolQueryBuilder;
    }


}
