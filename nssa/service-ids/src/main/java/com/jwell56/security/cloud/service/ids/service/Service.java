package com.jwell56.security.cloud.service.ids.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.util.FormatUtil;
import com.jwell56.security.cloud.service.ids.annotation.EntitySortOrder;
import com.jwell56.security.cloud.service.ids.annotation.TimeSearch;
import com.jwell56.security.cloud.service.ids.common.Pages;
import com.jwell56.security.cloud.service.ids.common.Pies;
import com.jwell56.security.cloud.service.ids.common.ReflectUtil;
import com.jwell56.security.cloud.service.ids.common.Times;
import com.jwell56.security.cloud.service.ids.common.Trends;

/**
 * @author wsg
 * @since 2020/12/2
 */
public abstract class Service<T> {
    T t;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    private static final String AGG_ITEM = "agg_item";

    public Map<String, Map<String, Object>> searchTrend(Trends trends, Times times) {
        Map<String, Object> totalPie = searchPie(trends, times);

        Map<String, Map<String, Object>> map = new LinkedHashMap<>();

        for (Times time : times.group(trends.getParts())) {

            Map<String, Object> pie = searchPie(new Pies(trends.getField(), trends.getCounts() + 100), time);

            //补齐key
            totalPie.keySet().stream()
                    .filter(key -> !pie.containsKey(key))
                    .forEach(key -> pie.put(key, 0L));

            //删除多余key
            new HashSet<>(pie.keySet()).stream()
                    .filter(key -> !totalPie.containsKey(key))
                    .forEach(key -> pie.remove(key));

            map.put(FormatUtil.DateTime(time.getStartTime()), pie);
        }

        return map;
    }

    public Map<String, Object> searchPie(Pies pies, Times times) {
        Map<String, Object> resMap = new LinkedHashMap<>();
        Class<?> entityClazz = ReflectUtil.getGenericsFieldClazz(getClass(), ".*entity.*");
        TableName annotation = entityClazz.getAnnotation(TableName.class);
        if (annotation == null) {
            return null;
        }
        SearchRequest searchRequest = new SearchRequest(annotation.value());
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //想要字段和去重复,默认返回10条
//        sourceBuilder.fetchSource("grade",null).collapse();
        //聚合
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        String fieldName = FormatUtil.DownToPoint(pies.getField());

        for (Field f : ReflectUtil.getFieldsWithSupper(entityClazz)) {
            if (pies.getField().equals(f.getName())) {
                if (f.getType().equals(String.class)) {
                    fieldName += ".keyword";
                }
            }
            f.setAccessible(true);
            TimeSearch timeSearch = f.getAnnotation(TimeSearch.class);
            if (timeSearch != null && timeSearch.value()) {

                if (times.getStartTime() == null || times.getEndTime() == null) {
                    continue;
                }
                //手动调整时区
                QueryBuilder queryBuilder = QueryBuilders.rangeQuery(FormatUtil.DownToPoint(f.getName()))
                        .from(times.getStartTime().minusHours(8))
                        .to(times.getEndTime().minusHours(8));
                boolQueryBuilder.must(queryBuilder);
            }

        }
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(AGG_ITEM).field(fieldName);
        aggregationBuilder.size(pies.getCounts());
        sourceBuilder.aggregation(aggregationBuilder);//聚合查询
        sourceBuilder.query(boolQueryBuilder);

        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);
        System.out.println(searchRequest.toString());

//        long total = 0;
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get(AGG_ITEM);
            Terms teamSum = (Terms) tags;
            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
            for (Terms.Bucket bucket : buckets) {
//                list.add(bucket.getKeyAsString());
                resMap.put(bucket.getKeyAsString(), bucket.getDocCount());
//                total += bucket.getDocCount();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        resMap.put("total", total);
        return resMap;
    }

    public List<String> searchDistinct(String filed) {
        List<String> list = new ArrayList<>();
        try {
            String esField = FormatUtil.DownToPoint(filed);
            // 1、创建search请求
            Class<?> entityClazz = ReflectUtil.getGenericsFieldClazz(getClass(), ".*entity.*");
            TableName annotation = entityClazz.getAnnotation(TableName.class);
            if (annotation == null) {
                return null;
            }
            SearchRequest searchRequest = new SearchRequest(annotation.value());
            // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            //想要字段和去重复,默认返回10条
            sourceBuilder.fetchSource(esField, null).collapse();
            //聚合
            TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(esField).field(esField + ".keyword");
            sourceBuilder.aggregation(aggregationBuilder);//聚合查询
            sourceBuilder.size(100);

            //将请求体加入到请求中
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get(esField);
            Terms teamSum = (Terms) tags;
            List<? extends Terms.Bucket> buckets = teamSum.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                list.add(bucket.getKeyAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public T searchDetail(String id) {

        T result = null;
        try {
            // 1、创建search请求
            Class<?> entityClazz = ReflectUtil.getGenericsFieldClazz(getClass(), ".*entity.*");
            TableName annotation = entityClazz.getAnnotation(TableName.class);
            if (annotation == null) {
                return null;
            }
            SearchRequest searchRequest = new SearchRequest(annotation.value());
            // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (id != null) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("_id", id);
                boolQueryBuilder.must(queryBuilder);
            }

            sourceBuilder.query(boolQueryBuilder);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            if (hits.getHits().length > 0) {
                Map<String, Object> sourceAsMap = hits.getHits()[0].getSourceAsMap(); // 取成map对象
                sourceAsMap.put("id", hits.getHits()[0].getId());
                result = mapToEntity(sourceAsMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Page<T> searchPage(T search, Pages pages) {
        return searchPage(search, null, pages);
    }

    public Page<T> searchPage(T search, Times times, Pages pages) {
        List<T> list = new ArrayList<>();
        Page page = new Page(pages.getPageNum(), pages.getPageSize());
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueSeconds(600));
            TableName annotation = search.getClass().getAnnotation(TableName.class);
            if (annotation == null) {
                return null;
            }
            SearchRequest searchRequest = new SearchRequest(annotation.value());
            searchRequest.scroll(scroll);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            sourceBuilder.size(pages.getPageSize());
            for (Field field : ReflectUtil.getFieldsWithSupper(search.getClass())) {
                //order by
                EntitySortOrder entitySortOrder = field.getAnnotation(EntitySortOrder.class);
                if (entitySortOrder != null) {
                    sourceBuilder.sort(FormatUtil.DownToPoint(field.getName()), entitySortOrder.sortOrder());
                }
            }

            sourceBuilder.query(makeQuery(search, times));
            searchRequest.source(sourceBuilder);

            //3、发送请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            //处理搜索命中文档结果
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;

            String scrollId = null;
            int pageNum = pages.getPageNum();
            int i = 1;
            while (searchResponse.getHits().getHits().length != 0) {
                if (i == pageNum) {
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                        Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象
                        sourceAsMap.put("id", hit.getId());
                        list.add(mapToEntity(sourceAsMap));
                    }
                    break;
                }

                i++;
                //每次循环完后取得scrollId,用于记录下次将从这个游标开始取数
                scrollId = searchResponse.getScrollId();
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
            }

            if (scrollId != null) {
                //清除滚屏
                ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                //也可以选择setScrollIds()将多个scrollId一起使用
                clearScrollRequest.addScrollId(scrollId);
                restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }

            page.setTotal(totalHits);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return (Page<T>) page.setRecords(list);
    }

    private Map<String, Object> mapTreeToMapList(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (String key : map.keySet()) {
            result.put(key, map.get(key));
            if (map.get(key) instanceof Map) {
                Map<String, Object> map2 = (Map<String, Object>) map.get(key);
                for (String key2 : map2.keySet()) {
                    result.put(key + "." + key2, map2.get(key2));
                }
            }
        }
        return result;
    }

    private T mapToEntity(Map<String, Object> map) {
        try {
            Class<?> entityClazz = ReflectUtil.getGenericsFieldClazz(getClass(), ".*entity.*");
            Object object = entityClazz.newInstance();
            Map<String, Object> flatMap = mapTreeToMapList(map);
            for (Field field : ReflectUtil.getFieldsWithSupper(entityClazz)) {
                field.setAccessible(true);
                if (field.getName().equals("raw")) {
                    field.set(object, JSONObject.toJSON(map).toString());
//                    log.info("======= raw :" + map.toString());
                    continue;
                }
                for (String key : flatMap.keySet()) {
                    if (key.equals(FormatUtil.DownToPoint(field.getName()))) {
                        try {
                            DateTimeFormat annotation = field.getAnnotation(DateTimeFormat.class);
                            if (annotation != null) {
                                String dateTime = (String) flatMap.get(key);
                                dateTime = dateTime.substring(0, 19);
                                dateTime = dateTime.replace("T", " ");
                                field.set(object, FormatUtil.StringToDateTime(dateTime));
                            } else {
                                field.set(object, flatMap.get(key));
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return (T) object;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected BoolQueryBuilder makeQuery(T search) {
        return makeQuery(search, null);
    }

    protected BoolQueryBuilder makeQuery(T search, Times times) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Field field : ReflectUtil.getFieldsWithSupper(search.getClass())) {
            Object object = null;
            try {
                field.setAccessible(true);
                object = field.get(search);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TimeSearch timeSearch = field.getAnnotation(TimeSearch.class);
            if (timeSearch == null || !timeSearch.value()) {
                //值为空、空字符、全部、0的情况下，不做处理
                if (object == null ||
                        (object instanceof String && ((String) object).isEmpty()) ||
                        (object instanceof String && object.equals("全部")) ||
                        (object instanceof Integer && object.equals(0))) {
                    continue;
                }
                String fieldName = FormatUtil.DownToPoint(field.getName());
                if (object instanceof String) {
                    fieldName += ".keyword";
                }
                if (fieldName.contains("raw")) {
                    String keywordAsc = QueryParser.escape(object.toString());
                    BoolQueryBuilder boolQueryBuilderAll = QueryBuilders.boolQuery();
                    //涉及到区域单位名称  资产和探针名称存在es中 就不需要查数据库


                    //matchPhraseQuery时，不会被分词器分词，而是直接以一个短语的形式查询 但是必须要指定file
//                    boolQueryBuilderAll.should(QueryBuilders.queryStringQuery(keywordAsc));
                    boolQueryBuilderAll.should(QueryBuilders.queryStringQuery("\"" + keywordAsc + "\""));
                    boolQueryBuilder.must(boolQueryBuilderAll);
                    continue;
                }
                fieldName = field.getName().equals("id") ? "_id" : fieldName;
                QueryBuilder queryBuilder = QueryBuilders.termQuery(fieldName, object);
                boolQueryBuilder.must(queryBuilder);
            } else {
                if (times == null || times.getTimeType() == null) {
                    continue;
                }
                //手动调整时区
                QueryBuilder queryBuilder = QueryBuilders.rangeQuery(FormatUtil.DownToPoint(field.getName()))
                        .from(times.getStartTime().minusHours(8))
                        .to(times.getEndTime().minusHours(8));
                boolQueryBuilder.must(queryBuilder);
            }

        }
        return boolQueryBuilder;
    }
}
