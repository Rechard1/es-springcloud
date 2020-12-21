package com.jwell56.security.cloud.service.apt.service.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.DesUtil;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.apt.entity.*;
import com.jwell56.security.cloud.service.apt.service.InNetService;
import com.jwell56.security.cloud.service.apt.service.IpToCountryService;
import com.jwell56.security.cloud.service.apt.service.feign.*;
import com.jwell56.security.cloud.service.apt.utils.GetAreaUnit;
import com.jwell56.security.cloud.service.apt.utils.IPUtil;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ESAptService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    private GetAreaUnit getAreaUnit;

    @Autowired
    private NetStructComponent netStructComponent;

    @Autowired
    private IAssetService iAssetService;

    @Autowired
    private IpToCountryService ipToCountryService;

    @Autowired
    private AssetComponent assetComponent;

    @Value("${agg}")
    private boolean useAgg;

    Logger logger = LoggerFactory.getLogger(ESAptService.class);

    public List<String> selectRiskType(String filed) {
        List<String> list = new ArrayList<>();
        try {
            // 1、创建search请求
            SearchRequest searchRequest = new SearchRequest("intrusion_result");
            // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
            //想要字段和去重复,默认返回10条
            sourceBuilder.fetchSource(filed, null).collapse();
            //聚合
            TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(filed).field(filed + ".keyword");
            sourceBuilder.aggregation(aggregationBuilder);//聚合查询
            sourceBuilder.size(100);

            //将请求体加入到请求中
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            Aggregations aggregations = searchResponse.getAggregations();
            Aggregation tags = aggregations.asMap().get(filed);
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

    //type会分词 模糊查询  keyword是关键字查询，不会分词  分词默认10条
    public Page<Apt> searchAptDoc(ParamPI paramPI) throws IOException, ParseException {
        Page page = new Page(paramPI.getPageNum(), paramPI.getPageSize());
        paramPI.setAreaUnit(getAreaUnit.getAreaUnit(paramPI));
        final Scroll scroll = new Scroll(TimeValue.timeValueSeconds(600));

        // 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        searchRequest.scroll(scroll);
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
//        sourceBuilder.timeout(TimeValue.timeValueSeconds(60)); //连接超时时间
        sourceBuilder.size(paramPI.getPageSize());
        sourceBuilder.sort("happen_time", SortOrder.DESC);
        //搜索条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (paramPI.getKeyword() != null) {
            String keyword = paramPI.getKeyword();
            String trueMacAddress1 = "([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}";
            String trueMacAddress2 = "([A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}";
            if(keyword.matches(trueMacAddress1) || keyword.matches(trueMacAddress2)){
                QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("smac.keyword", keyword))
                        .should(QueryBuilders.termQuery("dmac.keyword", keyword));
                boolQueryBuilder.must(queryBuilder);
            }else{
                String keywordAsc = QueryParser.escape(keyword);
                BoolQueryBuilder boolQueryBuilderAll = QueryBuilders.boolQuery();
                //涉及到区域单位名称  资产和探针名称存在es中 就不需要查数据库
                List<Integer> listArea = new ArrayList<>();
                List<Integer> listUnit = new ArrayList<>();
//                List<Integer> listAsset = new ArrayList<>();
//                List<Integer> listDevice = new ArrayList<>();
//                listAsset = assetComponent.searchByName(keyword);
//                if(listAsset!=null && !listAsset.isEmpty()){
//                    boolQueryBuilderAll.should(QueryBuilders.termsQuery("s_asset_id", listAsset))
//                            .should(QueryBuilders.termsQuery("d_asset_id", listAsset));
//                }
//                listDevice = assetComponent.searchByName(keyword);
//                if(listDevice!=null && !listDevice.isEmpty()){
//                    boolQueryBuilderAll.should(QueryBuilders.termsQuery("dvcid", listDevice));
//                }


                listArea = netStructComponent.searchAreaByName(keyword);
                if(listArea!=null && !listArea.isEmpty()){
                    boolQueryBuilderAll .should(QueryBuilders.termsQuery("s_area_id", listArea))
                                        .should(QueryBuilders.termsQuery("d_area_id", listArea))
                                        .should(QueryBuilders.termsQuery("dvcaid", listArea));

                }
                listUnit = netStructComponent.searchUnitByName(keyword);
                if(listUnit!=null && !listUnit.isEmpty()){
                    boolQueryBuilderAll.should(QueryBuilders.termsQuery("s_unit_id", listUnit))
                                       .should(QueryBuilders.termsQuery("d_unit_id", listUnit))
                                        .should(QueryBuilders.termsQuery("dvcuid", listUnit));
                }
                //matchPhraseQuery时，不会被分词器分词，而是直接以一个短语的形式查询 但是必须要指定file
//                boolQueryBuilderAll.should(QueryBuilders.queryStringQuery(keywordAsc));
                boolQueryBuilderAll.should(QueryBuilders.queryStringQuery("\"" + keywordAsc + "\""));
                boolQueryBuilder.must(boolQueryBuilderAll);
            }
        }
        if (paramPI.getRiskGrade() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("grade", paramPI.getRiskGrade());
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.getRiskType() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("types.keyword", paramPI.getRiskType());
            boolQueryBuilder.must(queryBuilder);
        }

        if (paramPI.getSip() != null) {
            String[] ip = paramPI.getSip().split("-");
            if (ip.length == 1) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("sipNum", (IPUtil.ipToLong(ip[0])));
                boolQueryBuilder.must(queryBuilder);
            }
            if (ip.length == 2) {
                QueryBuilder queryBuilder = QueryBuilders.rangeQuery("sipNum").from(IPUtil.ipToLong(ip[0])).to(IPUtil.ipToLong(ip[1]));
                boolQueryBuilder.must(queryBuilder);
            }
        }
        if (paramPI.getSmac() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("smac.keyword", paramPI.getSmac());
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.getSport() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("sport", paramPI.getSport());
            boolQueryBuilder.must(queryBuilder);
        }

        if (paramPI.getDip() != null) {
            String[] ip = paramPI.getDip().split("-");
            if (ip.length == 1) {
                QueryBuilder queryBuilder = QueryBuilders.termQuery("dipNum", (IPUtil.ipToLong(ip[0])));
                boolQueryBuilder.must(queryBuilder);
            }
            if (ip.length == 2) {
                QueryBuilder queryBuilder = QueryBuilders.rangeQuery("dipNum").from(IPUtil.ipToLong(ip[0])).to(IPUtil.ipToLong(ip[1]));
                boolQueryBuilder.must(queryBuilder);
            }
        }
        if (paramPI.getDmac() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("dmac.keyword", paramPI.getDmac());
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.getDport() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("dport", paramPI.getDport());
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.getRow() != null) {
            // 1、创建search请求
            SearchRequest searchRequestRe = new SearchRequest("intrusion_rowlog");
            // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
            SearchSourceBuilder sourceBuilderRe = new SearchSourceBuilder().trackTotalHits(true);
            BoolQueryBuilder boolQueryBuilderRe = QueryBuilders.boolQuery();

            String row = paramPI.getRow();
            row = QueryParser.escape(row);// 主要就是这一句把特殊字符都转义,那么lucene就可以识别
            QueryBuilder queryBuilderRes = QueryBuilders.queryStringQuery(row);
            boolQueryBuilderRe.must(queryBuilderRes);

            sourceBuilderRe.query(boolQueryBuilderRe);
            searchRequestRe.source(sourceBuilderRe);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequestRe, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            List<String> listRowkey = new ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象
                if (sourceAsMap.get("rowkey") != null) {
                    listRowkey.add((String) sourceAsMap.get("rowkey"));
                }
            }
            if (!listRowkey.isEmpty()) {
                QueryBuilder queryBuilder = QueryBuilders.termsQuery("rowkey", listRowkey);
                boolQueryBuilder.must(queryBuilder);
            }


//            QueryBuilder queryBuilder = QueryBuilders.termQuery("rowlog",paramPI.getRow());
//            boolQueryBuilder.must(queryBuilder);
//            QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(paramPI.getRow());
//            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> sAreaIdList = paramPI.getAreaUnit().getSAreaIdList();
        if (!sAreaIdList.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_area_id", sAreaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> sUnitIdList = paramPI.getAreaUnit().getSUnitIdList();
        if (!sUnitIdList.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_unit_id", sUnitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> dAreaIdList = paramPI.getAreaUnit().getDAreaIdList();
        if (!dAreaIdList.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_area_id", dAreaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> dUnitIdList = paramPI.getAreaUnit().getDUnitIdList();
        if (!dUnitIdList.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_unit_id", dUnitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> deviceAreaIdList = paramPI.getAreaUnit().getDeviceAreaIdList();
        if (!deviceAreaIdList.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("dvcaid", deviceAreaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> deviceUnitIdList = paramPI.getAreaUnit().getDeviceUnitIdList();
        if (!deviceUnitIdList.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("dvcuid", deviceUnitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.getDeviceId() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("dvcid", paramPI.getDeviceId());
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> areaIdListRole = paramPI.getAreaUnit().getAreaIdListRole();
        if (!areaIdListRole.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("s_area_id", areaIdListRole))
                    .should(QueryBuilders.termsQuery("d_area_id", areaIdListRole))
                    .should(QueryBuilders.termsQuery("dvcaid", areaIdListRole));
            boolQueryBuilder.must(queryBuilder);
        }
        List<Integer> unitIdListRole = paramPI.getAreaUnit().getUnitIdListRole();
        if (!unitIdListRole.isEmpty()) {
            QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termsQuery("s_unit_id", unitIdListRole))
                    .should(QueryBuilders.termsQuery("d_unit_id", unitIdListRole))
                    .should(QueryBuilders.termsQuery("dvcuid", unitIdListRole));
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.isImpAsset()) {
            QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .should(QueryBuilders.termQuery("s_important", 1))
                    .should(QueryBuilders.termQuery("d_important", 1));
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.isOutlandsAttack()) {
            List list = new ArrayList();
            list.add("CN");
            list.add("B1");
            list.add("");
            QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("scode.keyword", list))
                    .must(QueryBuilders.termsQuery("dcode.keyword", list));
            boolQueryBuilder.mustNot(queryBuilder);
        }
        if (paramPI.getEnterpriseId() != 0) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("enterprise_id", paramPI.getEnterpriseId());
            boolQueryBuilder.must(queryBuilder);
        }
        if (paramPI.getTimeParam() != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .from(TimeUtil.format(paramPI.getTimeParam().getStartTime()))
                    .to(TimeUtil.format(paramPI.getTimeParam().getEndTime()));
            boolQueryBuilder.must(queryBuilder);
        }

        if (paramPI.getRiskSort() != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("risk_sort.keyword", paramPI.getRiskSort());
            boolQueryBuilder.must(queryBuilder);
        }
        //聚合注意字段的属性 且需要关联上
        if (paramPI.isSum()) {
            TermsAggregationBuilder sipAggregation = AggregationBuilders.terms("group_by_sip").field("sip.keyword");
            TermsAggregationBuilder sipArea = AggregationBuilders.terms("group_by_sarea").field("s_area_id");
            TermsAggregationBuilder sipUnit = AggregationBuilders.terms("group_by_sunit").field("s_unit_id");

            TermsAggregationBuilder dipAggregation = AggregationBuilders.terms("group_by_dip").field("dip.keyword");
            TermsAggregationBuilder dipArea = AggregationBuilders.terms("group_by_darea").field("d_area_id");
            TermsAggregationBuilder dipUnit = AggregationBuilders.terms("group_by_dunit").field("d_unit_id");

            TermsAggregationBuilder types = AggregationBuilders.terms("group_by_types").field("types.keyword");

            sipAggregation.subAggregation(sipArea);
            sipArea.subAggregation(sipUnit);
            sipUnit.subAggregation(dipAggregation);
            dipAggregation.subAggregation(dipArea);
            dipArea.subAggregation(dipUnit);
            dipUnit.subAggregation(types);

            sipAggregation.size(10);
            sipArea.size(10);
            sipUnit.size(10);
            dipAggregation.size(10);
            dipArea.size(10);
            dipUnit.size(10);
            types.size(10);

            types.subAggregation(AggregationBuilders.topHits("apt_detail").size(10));

            sourceBuilder.aggregation(sipAggregation);
        }

        sourceBuilder.query(boolQueryBuilder);
        //将请求体加入到请求中
        searchRequest.source(sourceBuilder);

        //3、发送请求
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Apt> list = new ArrayList<>();

        if (paramPI.isSum()) {
            List<Apt> listSum = new ArrayList<>();
            System.err.println(searchResponse);

            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");

            JSONArray sipArray = aggregationsJson.getJSONObject("sterms#group_by_sip").getJSONArray("buckets");
            sipArray.forEach(sipObject -> {

                JSONArray sareaArray = ((JSONObject) sipObject).getJSONObject("lterms#group_by_sarea").getJSONArray("buckets");
                sareaArray.forEach(sareaObject -> {

                    JSONArray sunitArray = ((JSONObject) sareaObject).getJSONObject("lterms#group_by_sunit").getJSONArray("buckets");
                    sunitArray.forEach(sunitObject -> {

                        JSONArray dipArray = ((JSONObject) sunitObject).getJSONObject("sterms#group_by_dip").getJSONArray("buckets");
                        dipArray.forEach(dipObject -> {

                            JSONArray dareaArray = ((JSONObject) dipObject).getJSONObject("lterms#group_by_darea").getJSONArray("buckets");
                            dareaArray.forEach(dareaObject -> {

                                JSONArray dunitArray = ((JSONObject) dareaObject).getJSONObject("lterms#group_by_dunit").getJSONArray("buckets");
                                dunitArray.forEach(dunitObject -> {

                                    JSONArray typesArray = ((JSONObject) dunitObject).getJSONObject("sterms#group_by_types").getJSONArray("buckets");
                                    typesArray.forEach(typesObject -> {
                                        JSONObject typesJson = (JSONObject) typesObject;
                                        JSONObject aptdetail = typesJson.getJSONObject("top_hits#apt_detail").getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
                                                .getJSONObject("_source");

                                        Map<String, Object> data = new HashMap<>();
                                        Iterator it = aptdetail.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
                                            data.put(entry.getKey(), entry.getValue());
                                        }

                                        try {
                                            int counts = typesJson.getInteger("doc_count");
                                            setDataApt(data, listSum, counts);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    });

                                });

                            });

                        });

                    });

                });
            });
            if (!listSum.isEmpty()) {
                int countsum = listSum.size();
                page.setTotal(countsum);
                int i = (paramPI.getPageNum() - 1) * paramPI.getPageSize();
                for (int j = i; j < i + paramPI.getPageSize(); j++) {
                    if (j >= countsum) {
                        break;
                    }
                    list.add(listSum.get(j));
                }
            }
        } else {
            //处理搜索命中文档结果
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;

            String scrollId = null;
            int pageNum = paramPI.getPageNum();
            int i = 1;
            while (searchResponse.getHits().getHits().length != 0) {
                if (i == pageNum) {
                    //业务
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                        Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象
                        setDataApt(sourceAsMap, list, 0);
                    }
                    System.out.println("ES分页查询apt成功");
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
                ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            }

            page.setTotal(totalHits);
        }

        return (Page<Apt>) page.setRecords(list);
    }

    public String selectAptDetailDoc(String rowkey) throws IOException, ParseException {

        // 1、创建search请求
        SearchRequest searchRequest = new SearchRequest("intrusion_rowlog");
        // 2、用SearchSourceBuilder来构造查询请求体 ,请仔细查看它的方法，构造各种查询的方法都在这。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().trackTotalHits(true);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (rowkey != null) {
            QueryBuilder queryBuilder = QueryBuilders.termQuery("rowkey.keyword", rowkey);
            boolQueryBuilder.must(queryBuilder);
        }

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        String aptDetail = "";
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap(); // 取成map对象
            if (sourceAsMap.get("rowlog") != null) {
                aptDetail = (String) sourceAsMap.get("rowlog");
            }
        }

        return aptDetail;
    }

    public void setDataApt(Map<String, Object> map, List<Apt> list, int counts) throws ParseException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//可能Locale.ENGLISH
        Apt apt = new Apt();
        apt.setCounts(counts);
        SourceTargetInfo sourceTargetInfoS = new SourceTargetInfo();
        SourceTargetInfo sourceTargetInfoD = new SourceTargetInfo();
        if (map.get("rowkey") != null) {
            apt.setRowkey((String) map.get("rowkey"));
        }
        if (map.get("happen_time") != null) {
            apt.setHappenTime(LocalDateTime.parse((String) map.get("happen_time"), df));
        }
        if (map.get("sip") != null) {
            apt.setSip((String) map.get("sip"));
        }
        if (map.get("smac") != null) {
            apt.setSmac((String) map.get("smac"));
        }
        if (map.get("sport") != null) {
            apt.setSport((String) map.get("sport"));
        }
        if (map.get("s_area_id") != null && (Integer) map.get("s_area_id") != 0) {
            String sAreaName = netStructComponent.getAreaName((Integer) map.get("s_area_id"));
            apt.setSAreaName(sAreaName);
            sourceTargetInfoS.setAreaName(sAreaName);
        }
        if (map.get("s_unit_id") != null && (Integer) map.get("s_unit_id") != 0) {
            String sUnitName = netStructComponent.getUnitName((Integer) map.get("s_unit_id"));
            apt.setSUnitName(sUnitName);
            sourceTargetInfoS.setUnitName(sUnitName);
        }
        if (map.get("s_asset_id") != null && (Integer) map.get("s_asset_id") != 0) {
            apt.setSAssetId((Integer) map.get("s_asset_id"));
            ResultObject resultObject = iAssetService.getAssetById((Integer) map.get("s_asset_id"));
            Map asset = (Map) resultObject.getData();
            if (asset != null) {
                apt.setSAssetName((String) asset.get("name"));
                apt.setSipImportant((Integer) asset.get("important"));
                sourceTargetInfoS.setName((String) asset.get("name"));
                sourceTargetInfoS.setType((String) asset.get("type"));
                if (asset.get("principal") != null) {
                    sourceTargetInfoS.setPeople((String) asset.get("principal"));
                }
            }
        }
        //地区
        if (map.get("scode") != null && !map.get("scode").equals("")) {
            if (map.get("sip") != null) {
                if (map.get("scode").equals("B1")) {
                    apt.setSDomain("保留IP");
                } else {
                    long sipNum = IPUtil.ipToLong((String) map.get("sip"));
                    QueryWrapper<IpToCountry> queryWrapper = new QueryWrapper();
                    queryWrapper.lambda().le(IpToCountry::getMinip, sipNum);
                    queryWrapper.last("limit 1");
                    queryWrapper.orderByDesc("minip");
                    IpToCountry ipToCountry = ipToCountryService.getOne(queryWrapper);
                    if (ipToCountry != null) {
                        String str = "";
                        if (ipToCountry.getCountry() != null) {
                            str = str + ipToCountry.getCountry();
                        }
                        str = str + "-";
                        if (ipToCountry.getProvince() != null) {
                            str = str + ipToCountry.getProvince();
                        }
                        str = str + "-";
                        if (ipToCountry.getCity() != null) {
                            str = str + ipToCountry.getCity();
                        }
                        apt.setSDomain(str);
                    }

                    BeanUtils.copyProperties(ipToCountry, sourceTargetInfoS);
                }

            }
        }

        if (map.get("dip") != null) {
            apt.setDip((String) map.get("dip"));
        }
        if (map.get("dmac") != null) {
            apt.setDmac((String) map.get("dmac"));
        }
        if (map.get("dport") != null) {
            apt.setDport((String) map.get("dport"));
        }
        if (map.get("d_area_id") != null && (Integer) map.get("d_area_id") != 0) {
            String dAreaName = netStructComponent.getAreaName((Integer) map.get("d_area_id"));
            apt.setDAreaName(dAreaName);
            sourceTargetInfoD.setAreaName(dAreaName);
        }
        if (map.get("d_unit_id") != null && (Integer) map.get("d_unit_id") != 0) {
            String dUnitName = netStructComponent.getUnitName((Integer) map.get("d_unit_id"));
            apt.setDUnitName(dUnitName);
            sourceTargetInfoD.setUnitName(dUnitName);
        }
        if (map.get("d_asset_id") != null && (Integer) map.get("d_asset_id") != 0) {
            apt.setDAssetId((Integer) map.get("d_asset_id"));
            ResultObject resultObject = iAssetService.getAssetById((Integer) map.get("d_asset_id"));
            Map asset = (Map) resultObject.getData();
            if (asset != null) {
                apt.setDAssetName((String) asset.get("name"));
                apt.setDipImportant((Integer) asset.get("important"));
                sourceTargetInfoD.setName((String) asset.get("name"));
                sourceTargetInfoD.setType((String) asset.get("type"));
                if (asset.get("principal") != null) {
                    sourceTargetInfoD.setPeople((String) asset.get("principal"));
                }
            }
        }
        if (map.get("dcode") != null && !map.get("dcode").equals("")) {
            if (map.get("dip") != null) {
                if (map.get("dcode").equals("B1")) {
                    apt.setDDomain("保留IP");
                } else {
                    long dipNum = IPUtil.ipToLong((String) map.get("dip"));
                    QueryWrapper<IpToCountry> queryWrapper = new QueryWrapper();
                    queryWrapper.lambda().le(IpToCountry::getMinip, dipNum);
                    queryWrapper.last("limit 1");
                    queryWrapper.orderByDesc("minip");
                    IpToCountry ipToCountry = ipToCountryService.getOne(queryWrapper);
                    if (ipToCountry != null) {
                        String str = "";
                        if (ipToCountry.getCountry() != null) {
                            str = str + ipToCountry.getCountry();
                        }
                        str = str + "-";
                        if (ipToCountry.getProvince() != null) {
                            str = str + ipToCountry.getProvince();
                        }
                        str = str + "-";
                        if (ipToCountry.getCity() != null) {
                            str = str + ipToCountry.getCity();
                        }
                        apt.setDDomain(str);
                    }

                    BeanUtils.copyProperties(ipToCountry, sourceTargetInfoD);
                }
            }
        }
        apt.setSourceTargetInfoS(sourceTargetInfoS);
        apt.setSourceTargetInfoD(sourceTargetInfoD);
        if (map.get("types") != null) {
            apt.setRiskType((String) map.get("types"));
        }
        if (map.get("grade") != null) {
            apt.setRiskGrade((String) map.get("grade"));
        }

        if (map.get("dvcaid") != null && (Integer) map.get("dvcaid") != 0) {
            apt.setDeviceAreaName(netStructComponent.getAreaName((Integer) map.get("dvcaid")));
        }
        if (map.get("dvcuid") != null && (Integer) map.get("dvcuid") != 0) {
            apt.setDeviceUnitName(netStructComponent.getUnitName((Integer) map.get("dvcuid")));
        }
        if (map.get("device") != null) {
            apt.setDeviceName((String) map.get("device"));
        }

        if (map.get("risk_sort") != null) {
            apt.setRiskSort((String) map.get("risk_sort"));
        }

        list.add(apt);
    }

    public List<Map<String, Integer>> aptTrendStatistic(Integer dataType,
                                                        List<Integer> areaIdList,
                                                        List<Integer> unitIdList,
                                                        String deviceType,
                                                        TimeParam timeParam) {


        System.err.println("Area List: " + areaIdList);
        System.err.println("Unit List: " + unitIdList);
        System.err.println("Data type: " + dataType);
        System.err.println("Time param: " + timeParam);


        LocalDateTime startTime = timeParam.getStartTime();
        LocalDateTime endTime = timeParam.getEndTime();

        List<Map<String, Integer>> dataList = new ArrayList<>();
        Map<String, Integer> totalMap = new LinkedHashMap<>();
        Map<String, Integer> lowMap = new LinkedHashMap<>();
        Map<String, Integer> middleMap = new LinkedHashMap<>();
        Map<String, Integer> highMap = new LinkedHashMap<>();
        dataList.add(totalMap);
        dataList.add(highMap);
        dataList.add(middleMap);
        dataList.add(lowMap);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (TimeUtil timeUtil : timeParam.group()) {
            LocalDateTime start = timeUtil.getStartTime();
            LocalDateTime end = timeUtil.getEndTime();

            CountRequest countRequest1 = new CountRequest("intrusion_result");
            BoolQueryBuilder lowBoolQueryBuilder = generateTrendPublicQueryBuilder(dataType, areaIdList, unitIdList, deviceType);

            lowBoolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_LOW));
            lowBoolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(end))
                    .gte(df.format(start)));

            countRequest1.query(lowBoolQueryBuilder);
            CountResponse lowResponse = null;
            try {
                lowResponse = restHighLevelClient.count(countRequest1, RequestOptions.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Integer low = Math.toIntExact(lowResponse.getCount());
            lowMap.put(df.format(start), low);


            CountRequest countRequest2 = new CountRequest("intrusion_result");
            BoolQueryBuilder midBoolQueryBuilder = generateTrendPublicQueryBuilder(dataType, areaIdList, unitIdList, deviceType);

            midBoolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_MID));
            midBoolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(end))
                    .gte(df.format(start)));

            countRequest2.query(midBoolQueryBuilder);
            CountResponse middleResponse = null;
            try {
                middleResponse = restHighLevelClient.count(countRequest2, RequestOptions.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Integer middle = Math.toIntExact(middleResponse.getCount());

            middleMap.put(df.format(start), middle);


            CountRequest countRequest3 = new CountRequest("intrusion_result");
            BoolQueryBuilder highBoolQueryBuilder = generateTrendPublicQueryBuilder(dataType, areaIdList, unitIdList, deviceType);
            highBoolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_HIGH));
            highBoolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(end))
                    .gte(df.format(start)));
            countRequest3.query(highBoolQueryBuilder);
            CountResponse highResponse = null;
            try {
                highResponse = restHighLevelClient.count(countRequest3, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Integer high = Math.toIntExact(highResponse.getCount());
            highMap.put(df.format(start), high);

            totalMap.put(df.format(start), low + middle + high);
        }


        return dataList;
    }


    /**
     * author Richard Yao
     * generate summary pie data of apt
     *
     * @param areaIdList
     * @param unitIdList
     * @param deviceType
     * @param timeParam
     * @return
     */
    public List<Map<String, Object>> getPie(List<Integer> areaIdList,
                                            List<Integer> unitIdList,
                                            String deviceType,
                                            TimeParam timeParam
    ) {

        List<Map<String, Object>> AptList = new LinkedList<>();
        LocalDateTime startTime = timeParam.getStartTime();
        LocalDateTime endTime = timeParam.getEndTime();

//        if (useEx(startTime, endTime)) {
//            return getPieEx(startTime, endTime, areaIdList, unitIdList, deviceType);
//        }

        if (areaIdList == null || unitIdList == null) {
            return AptList;
        }

        SearchRequest searchRequest = new SearchRequest("intrusion_result");
//        searchRequest.searchType("_doc");

        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_types").field("types.keyword");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        CountRequest countRequest = new CountRequest("intrusion_result");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }
//        QueryBuilder sourceQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
//                .should(QueryBuilders.termsQuery("s_unit_id", unitIdList));
//        QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("d_area_id", areaIdList))
//                .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));
//
//
//        boolQueryBuilder.must(sourceQueryBuilder);
//        boolQueryBuilder.must(destinationQueryBuilder);

        QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
                .should(QueryBuilders.termsQuery("s_unit_id", unitIdList))
                .should(QueryBuilders.termsQuery("d_area_id", areaIdList))
                .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));

        boolQueryBuilder.must(destinationQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        JSONArray countArray = new JSONArray();

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
            countArray = aggregationsJson.getJSONObject("sterms#group_by_types").getJSONArray("buckets");

        } catch (IOException e) {
            e.printStackTrace();
        }

        countArray.forEach(aggregations -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", ((JSONObject) aggregations).getInteger("doc_count"));
            map.put("attack_type", ((JSONObject) aggregations).getString("key"));
            AptList.add(map);
        });
        return AptList;

    }

    private List<Map<String, Object>> getPieEx(LocalDateTime startTime, LocalDateTime endTime,
                                               List<Integer> areaIdList, List<Integer> unitIdList, String deviceType) {
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_types").field("types.keyword");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }


        final boolean searchSIp = true;
        final boolean searchDIp = true;

        if (areaIdList != null && searchSIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (areaIdList != null && searchDIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchSIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchDIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }

//        if (searchSIp && searchDIp) {
//            if (iAssetService.getIpListByDeviceType(deviceType) != null) {
//                QueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                        .should(QueryBuilders.termsQuery("sip", iAssetService.getIpListByDeviceType(deviceType)))
//                        .should(QueryBuilders.termsQuery("dip", iAssetService.getIpListByDeviceType(deviceType)));
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchSIp) {
//
//            QueryBuilder queryBuilder = QueryBuilders.termsQuery("sip", iAssetService.getIpListByDeviceType(deviceType));
//            boolQueryBuilder.must(queryBuilder);
//        } else if (searchDIp) {
//            QueryBuilder queryBuilder = QueryBuilders.termsQuery("dip", iAssetService.getIpListByDeviceType(deviceType));
//            boolQueryBuilder.must(queryBuilder);
//        }


        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        JSONArray countArray = new JSONArray();

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
            countArray = aggregationsJson.getJSONObject("sterms#group_by_types").getJSONArray("buckets");

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> AptList = new LinkedList<>();
        countArray.forEach(aggregations -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", ((JSONObject) aggregations).getInteger("doc_count"));
            map.put("attack_type", ((JSONObject) aggregations).getString("key"));
            AptList.add(map);
        });
        return AptList;
    }

    /**
     * //TODO 长时间特殊处理，会导致失真，丢失当天数据
     */
    private boolean useEx(LocalDateTime startTime, LocalDateTime endTime) {
//        return false;
        return useAgg && endTime.isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
//        return startTime.plusDays(60).isBefore(endTime);
    }


    /**
     * author Richard Yao
     * get summary pie data of grade in apt
     *
     * @param areaIdList
     * @param unitIdList
     * @param deviceType
     * @param timeParam
     * @return
     */
    public List<Map<String, Object>> getGradeList(List<Integer> areaIdList,
                                                  List<Integer> unitIdList,
                                                  String deviceType,
                                                  TimeParam timeParam) {

        List<Map<String, Object>> AptList = new LinkedList<>();
        LocalDateTime startTime = timeParam.getStartTime();
        LocalDateTime endTime = timeParam.getEndTime();

        if (useEx(startTime, endTime)) {
            return getPieEx(startTime, endTime, areaIdList, unitIdList, deviceType);
        }

        if (areaIdList == null || unitIdList == null) {
            return AptList;
        }


        SearchRequest searchRequest = new SearchRequest("intrusion_result");
//        searchRequest.searchType("_doc");

        TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_grade").field("grade.keyword");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregation);
        searchSourceBuilder.size(0);

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        CountRequest countRequest = new CountRequest("intrusion_result");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }
//        QueryBuilder sourceQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
//                .should(QueryBuilders.termsQuery("s_unit_id", unitIdList));
//
//        QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("d_area_id", areaIdList))
//                .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));
//
//        boolQueryBuilder.must(sourceQueryBuilder);
//        boolQueryBuilder.must(destinationQueryBuilder);
        QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
                .should(QueryBuilders.termsQuery("s_unit_id", unitIdList))
                .should(QueryBuilders.termsQuery("d_area_id", areaIdList))
                .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));

        boolQueryBuilder.must(destinationQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        JSONArray countArray = new JSONArray();

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
            countArray = aggregationsJson.getJSONObject("sterms#group_by_grade").getJSONArray("buckets");

        } catch (IOException e) {
            e.printStackTrace();
        }

        countArray.forEach(aggregations -> {

            Map<String, Object> map = new HashMap<>();
            map.put("value", ((JSONObject) aggregations).getInteger("doc_count"));
            map.put("attack_grade", ((JSONObject) aggregations).getString("key"));
            AptList.add(map);
        });
        return AptList;

    }


    /**
     * 描述
     *
     * @param dataType
     * @param timeParam
     * @param areaIdList
     * @param unitIdList
     * @param deviceType
     * @return
     * @author Richard Yao
     */
    public String getDesData(Integer dataType, TimeParam timeParam, List<Integer> areaIdList, List<Integer> unitIdList, String deviceType) {
        List<Map<String, Integer>> dataList = aptTrendStatistic(dataType, areaIdList, unitIdList, deviceType, timeParam);
//        List<String> deviceIps = (List<String>) iAssetService.getIpListByDeviceType(deviceType).getData();
        List<String> deviceIps = null;
        String des = "";
        if (dataList != null && dataList.size() == 4) {
            Integer attackCount = 0;
            Integer attackedCount = 0;
            if (dataType == 0) {

//                Map<String, Integer> resultMap = new HashMap<>();

//                resultMap.put("attack", getAttackCount(areaIdList, unitIdList, deviceType, timeParam));
//                resultMap.put("attacked", getAttackedCount(areaIdList, unitIdList, deviceType, timeParam));
//                redisUtil.set(cacheKey, resultMap, GlobalDataCache.CACHE_TIME);

//                Map<String, Integer> attackMap = this.getAttackCache(timeParam, auParam, deviceType);
//                attackCount = attackMap.get("attack");
//                attackedCount = attackMap.get("attacked");

                attackCount = getAttackCount(areaIdList, unitIdList, deviceType, timeParam, deviceIps);
                attackedCount = getAttackedCount(areaIdList, unitIdList, deviceType, timeParam, deviceIps);
            }
            Integer trendType = dataType == 0 ? -1 : dataType;
            Integer his = getHistoryCount(trendType, areaIdList, unitIdList,
                    timeParam.hisStart(), timeParam.hisEnd(), null, deviceType);
            des = getDes(dataType, timeParam.getTimeType(), getMapCount(dataList.get(1)), getMapCount(dataList.get(2)),
                    getMapCount(dataList.get(3)), his, attackCount, attackedCount);
        }
        return des;
    }


    public Integer getAttackedCount(List<Integer> areaIdList,
                                    List<Integer> unitIdList,
                                    String deviceType,
                                    TimeParam timeParam,
                                    List<String> deviceIps) {
        LocalDateTime startTime = timeParam.getStartTime();
        LocalDateTime endTime = timeParam.getEndTime();

        Integer dataType = 2;
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        TermsAggregationBuilder dipAggregation = AggregationBuilders.terms("group_by_Dip").field("dip.keyword");

        dipAggregation.size(100000);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(dipAggregation);
        searchSourceBuilder.size(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();


        QueryBuilder queryBuilders = QueryBuilders.existsQuery("dvcid");
        boolQueryBuilder.must(queryBuilders);

        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }
        //被攻击（dip为内网）
//        boolQueryBuilder.must(inNetService.queryWrapperForDipIn());
        boolQueryBuilder.must(QueryBuilders.termQuery("dcode.keyword", ""));

        boolQueryBuilder.must(QueryBuilders.termsQuery("d_area_id", areaIdList));
        boolQueryBuilder.must(QueryBuilders.termsQuery("d_unit_id", unitIdList));

        if (deviceIps != null) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("dip", deviceIps);
            boolQueryBuilder.must(queryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        JSONArray countArray = new JSONArray();

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
            countArray = aggregationsJson.getJSONObject("sterms#group_by_Dip").getJSONArray("buckets");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return countArray.size();
    }


    public Integer getAttackCount(List<Integer> areaIdList,
                                  List<Integer> unitIdList,
                                  String deviceType,
                                  TimeParam timeParam,
                                  List<String> deviceIps) {

        LocalDateTime startTime = timeParam.getStartTime();
        LocalDateTime endTime = timeParam.getEndTime();

        Integer dataType = 1;
        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        TermsAggregationBuilder dipAggregation = AggregationBuilders.terms("group_by_Dip").field("dip.keyword");
//        TermsAggregationBuilder deviceIdAggregation = AggregationBuilders.terms("group_by_DeviceId").field("dvcid.keyword");

//        dipAggregation.subAggregation(deviceIdAggregation);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(dipAggregation);
        searchSourceBuilder.size(0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

//        QueryBuilder queryBuilders = QueryBuilders.existsQuery("dvcid");
//        boolQueryBuilder.must(queryBuilders);
        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }

        //sip为内网（主动攻击）
        boolQueryBuilder.must(QueryBuilders.termQuery("scode.keyword", ""));

        boolQueryBuilder.must(QueryBuilders.termsQuery("s_area_id", areaIdList));
        boolQueryBuilder.must(QueryBuilders.termsQuery("s_unit_id", unitIdList));


        if (deviceIps != null) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("sip", deviceIps);
            boolQueryBuilder.must(queryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        JSONArray countArray = new JSONArray();

        try {
            System.err.println(searchRequest);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.err.println(searchResponse);

            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
            countArray = aggregationsJson.getJSONObject("sterms#group_by_Dip").getJSONArray("buckets");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return countArray.size();
    }


    public Integer getHistoryCount(Integer dataType, List<Integer> areaIdList, List<Integer> unitIdList,
                                   LocalDateTime startTime, LocalDateTime endTime, String attackStage, String deviceType) {

        CountRequest countRequest = new CountRequest("intrusion_result");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }


        if (dataType != null && dataType == 1) {//主动攻击(sip为内网)

            boolQueryBuilder.must(QueryBuilders.termQuery("scode.keyword", ""));
        }

        if (dataType != null && dataType == 2) {//被攻击（dip为内网）
//            QueryBuilder queryBuilder = inNetService.queryWrapperForDipIn();
//            boolQueryBuilder.must(queryBuilder);
            boolQueryBuilder.must(QueryBuilders.termQuery("dcode.keyword", ""));
        }

        if (dataType != null && dataType == 3) {//外网攻击（sip为外网）
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("scode.keyword", ""));

        }


        final boolean searchSIp = dataType == null || dataType == 1;
        final boolean searchDIp = dataType == null || dataType == 2;

        if (!searchSIp && !searchDIp) {

            QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
                    .should(QueryBuilders.termsQuery("s_unit_id", unitIdList))
                    .should(QueryBuilders.termsQuery("d_area_id", areaIdList))
                    .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));

            boolQueryBuilder.must(destinationQueryBuilder);

        }

//            if (!searchSIp && !searchDIp) {//如果不需要查询则直接返回
//                queryWrapper.in("area_id", areaIdList);
//                queryWrapper.in("unit_id", unitIdList);
//                return queryWrapper;
//            }

        if (areaIdList != null && searchSIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (areaIdList != null && searchDIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchSIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchDIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }

//        List<String> deviceIps = (List<String>) iAssetService.getIpListByDeviceType(deviceType).getData();
//        if (searchSIp && searchDIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                        .should(QueryBuilders.termsQuery("sip", deviceIps))
//                        .should(QueryBuilders.termsQuery("dip", deviceIps));
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchSIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.termsQuery("sip", deviceIps);
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchDIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.termsQuery("dip", deviceIps);
//                boolQueryBuilder.must(queryBuilder);
//            }
//        }


//        if (attackStage != null) {
//            boolQueryBuilder.must(QueryBuilders.termQuery("grade", attackStage));
//        }

        countRequest.query(boolQueryBuilder);
        CountResponse countResponse = null;
        try {
            countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            System.err.println(countResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Integer count = Math.toIntExact(countResponse.getCount());

        return count;


    }

    /**
     * 生成描述文字
     * dataType=0 整体安全趋势
     * 今日威胁攻击行为：n次，相比同期减少n次，减少率n%，其中威胁主动攻击设备n台，被攻击设备n台，
     * 威胁攻击评级中，高危n次，中危n次，低危n次，如下图：
     * <p>
     * dataType=1 主动攻击设备统计
     * 今日威胁主动攻击行为：n次，相比同期减少n次，减少率n%，威胁攻击评级中，高危n次，中危n次，低危n次，如下图：
     * <p>
     * dataType=2 主动攻击设备统计
     * 今日威胁被攻击：n次，相比同期减少n次，减少率n%，威胁攻击评级中，高危n次，中危n次，低危n次，如下图：
     * <p>
     * dataType=3 外网威胁IP统计
     * 今日外网攻击行为：n次，相比同期减少n次，减少率n%，威胁攻击评级中，高危n次，中危n次，低危n次，如下图：
     */
    private String getDes(Integer dataType, Integer timeType, Integer nowHigh, Integer nowMid, Integer nowLow,
                          Integer hisTotal, Integer attackCount, Integer attackedCount) {
        Integer nowTotal = nowHigh + nowMid + nowLow;

        DesUtil desUtils = new DesUtil(timeType, nowTotal, hisTotal).invoke();
        String des = "";
        switch (dataType) {
            case 0:
                des = String.format("%s威胁行为：%d次，相比%s%s%d次，%s率%s%%，其中威胁主动攻击设备%d台，被攻击设备%d台，" +
                                "威胁攻击评级中，高危%d次，中危%d次，低危%d次，如下图：",
                        desUtils.getDesTime(), nowTotal, desUtils.getDesPeriod(), desUtils.getDesCrease(), desUtils.getSub(), desUtils.getDesCrease(),
                        desUtils.getPerStr(), attackCount, attackedCount, nowHigh, nowMid, nowLow);
                break;
            case 1:
                des = String.format("%s威胁主动攻击行为：%d次，相比%s%s%d次，%s率%s%%，威胁攻击评级中，高危%d次，中危%d次，低危%d次，如下图：",
                        desUtils.getDesTime(), nowTotal, desUtils.getDesPeriod(), desUtils.getDesCrease(), desUtils.getSub(), desUtils.getDesCrease(),
                        desUtils.getPerStr(), nowHigh, nowMid, nowLow);
                break;
            case 2:
                des = String.format("%s威胁被攻击：%d次，相比%s%s%d次，%s率%s%%，威胁攻击评级中，高危%d次，中危%d次，低危%d次，如下图：",
                        desUtils.getDesTime(), nowTotal, desUtils.getDesPeriod(), desUtils.getDesCrease(), desUtils.getSub(), desUtils.getDesCrease(),
                        desUtils.getPerStr(), nowHigh, nowMid, nowLow);
                break;
            case 3:
                des = String.format("%s外网攻击行为：%d次，相比%s%s%d次，%s率%s%%，威胁攻击评级中，高危%d次，中危%d次，低危%d次，如下图：",
                        desUtils.getDesTime(), nowTotal, desUtils.getDesPeriod(), desUtils.getDesCrease(), desUtils.getSub(), desUtils.getDesCrease(),
                        desUtils.getPerStr(), nowHigh, nowMid, nowLow);
                break;
            default:
                break;

        }
        return des;
    }

    int getMapCount(Map<String, Integer> map) {
        int count = 0;
        for (Integer i : map.values()) {
            count += i;
        }
        return count;
    }


//    public IPage<Map<String, Object>> getTop(Integer dataType, Integer n) {

    /**
     * fetch report top table data
     *
     * @param dataType
     * @param areaIdList
     * @param unitIdList
     * @param n
     * @param deviceType
     * @param timeParam
     * @return
     * @author Richard Yao
     */
    public List<Map<String, Object>> getTop(Integer dataType,
                                            List<Integer> areaIdList,
                                            List<Integer> unitIdList,
                                            Integer n,
                                            String deviceType,
                                            TimeParam timeParam) {

        System.err.println("dataType:  " + dataType);

        LocalDateTime startTime = timeParam.getStartTime();
        LocalDateTime endTime = timeParam.getEndTime();
        SearchRequest searchRequest = new SearchRequest("intrusion_result");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (startTime != null && endTime != null) {
            QueryBuilder queryBuilder = QueryBuilders.rangeQuery("happen_time")
                    .lte(df.format(endTime))
                    .gte(df.format(startTime));
            boolQueryBuilder.must(queryBuilder);
        }


        if (dataType != null && dataType == 1) {//主动攻击(sip为内网)

            boolQueryBuilder.must(QueryBuilders.termQuery("scode.keyword", ""));
        }

        if (dataType != null && dataType == 2) {//被攻击（dip为内网）
//            QueryBuilder queryBuilder = inNetService.queryWrapperForDipIn();
//            boolQueryBuilder.must(queryBuilder);
            boolQueryBuilder.must(QueryBuilders.termQuery("dcode.keyword", ""));
        }

        if (dataType != null && dataType == 3) {//外网攻击（sip为外网）
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("scode.keyword", ""));

        }


        final boolean searchSIp = dataType == null || dataType == 1;
        final boolean searchDIp = dataType == null || dataType == 2;

        if (!searchSIp && !searchDIp) {

            QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
                    .should(QueryBuilders.termsQuery("s_unit_id", unitIdList))
                    .should(QueryBuilders.termsQuery("d_area_id", areaIdList))
                    .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));

            boolQueryBuilder.must(destinationQueryBuilder);

        }

        if (areaIdList != null && searchSIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (areaIdList != null && searchDIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchSIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchDIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }

//        List<String> deviceIps = (List<String>) iAssetService.getIpListByDeviceType(deviceType).getData();
//        if (searchSIp && searchDIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                        .should(QueryBuilders.termsQuery("sip", deviceIps))
//                        .should(QueryBuilders.termsQuery("dip", deviceIps));
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchSIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.termsQuery("sip", deviceIps);
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchDIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.termsQuery("dip", deviceIps);
//                boolQueryBuilder.must(queryBuilder);
//            }
//        }


        if (n == null || n.equals(0)) {
            n = 10;
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(0);
        if (dataType != null) {
            TermsAggregationBuilder sipAggregation = AggregationBuilders.terms("group_by_ip").field("sip.keyword");
            TermsAggregationBuilder dipAggregation = AggregationBuilders.terms("group_by_ip").field("dip.keyword");
            if (dataType.equals(0)) {//危险主机top10，高危+主动攻击
//                aptQueryWrapper.select("*", "count(1) as counts", "s_ip as ip");
                TermsAggregationBuilder deviceIpAggregation = AggregationBuilders.terms("group_by_device_ip").field("dvcip.keyword");

                sipAggregation.subAggregation(deviceIpAggregation);

                //todo 应该按照最后的聚合字段作全数据展示
                sipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                sipAggregation.size(n);
                QueryBuilder queryBuilder = QueryBuilders.termsQuery("grade", Apt.ATTACK_STAGE_HIGH);
                searchSourceBuilder.aggregation(sipAggregation);
                boolQueryBuilder.must(queryBuilder);

            } else if (dataType.equals(1)) {//主动攻击top10

                TermsAggregationBuilder deviceIpAggregation = AggregationBuilders.terms("group_by_device_ip").field("dvcip.keyword");
                sipAggregation.subAggregation(deviceIpAggregation);
//                sipAggregation.subAggregation(AggregationBuilders.count("counts").field("sip"));
                sipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                sipAggregation.size(n);
                searchSourceBuilder.aggregation(sipAggregation);

            } else if (dataType.equals(2)) {//被动攻击top10

                TermsAggregationBuilder deviceIpAggregation = AggregationBuilders.terms("group_by_device_ip").field("dvcip.keyword");
                dipAggregation.subAggregation(deviceIpAggregation);
//                dipAggregation.subAggregation(AggregationBuilders.count("counts").field("dip"));
                dipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                dipAggregation.size(n);
                searchSourceBuilder.aggregation(dipAggregation);
            } else if (dataType.equals(3)) {//外网攻击top10，外网的搜索条件在factorAptQueryWrapper里面

                TermsAggregationBuilder deviceIpAggregation = AggregationBuilders.terms("group_by_device_ip").field("dvcip.keyword");
                dipAggregation.subAggregation(deviceIpAggregation);
                sipAggregation.subAggregation(dipAggregation);
//                sipAggregation.subAggregation(AggregationBuilders.count("counts").field("sip"));
                sipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                sipAggregation.size(n);
                searchSourceBuilder.aggregation(sipAggregation);

//                searchSourceBuilder.sort(new FieldSortBuilder("counts.value").order(SortOrder.DESC));

            } else if (dataType.equals(5)) {//内网IP攻击统计

                sipAggregation.subAggregation(dipAggregation);
//                sipAggregation.subAggregation(AggregationBuilders.count("counts").field("sip"));
                sipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                sipAggregation.size(n);
                searchSourceBuilder.aggregation(sipAggregation);

            } else if (dataType.equals(6)) {//外网IP攻击统计

                sipAggregation.subAggregation(dipAggregation);
//                sipAggregation.subAggregation(AggregationBuilders.count("counts").field("sip"));
                sipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                sipAggregation.size(n);
                searchSourceBuilder.aggregation(sipAggregation);

            } else if (dataType.equals(7)) {//内网IP被攻击统计

                sipAggregation.subAggregation(dipAggregation);
//                sipAggregation.subAggregation(AggregationBuilders.count("counts").field("sip"));

                sipAggregation.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
                sipAggregation.size(n);
                searchSourceBuilder.aggregation(sipAggregation);

            }
        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        JSONArray countArray = new JSONArray();

        try {
            System.err.println("search request top : " + searchRequest);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.err.println("search response top : " + searchResponse);

            JSONObject aggregationsJson = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");
            countArray = aggregationsJson.getJSONObject("sterms#group_by_ip").getJSONArray("buckets");

        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Map<String, Object>> aptList = new ArrayList<>();

        if (countArray.size() != 0 || countArray != null) {
            countArray.forEach(groupCount -> {

                JSONObject groupCountJson = (JSONObject) groupCount;

                JSONObject aptdetail = groupCountJson.getJSONObject("top_hits#apt_detail").getJSONObject("hits").getJSONArray("hits").getJSONObject(0)
                        .getJSONObject("_source");
                Map<String, Object> map = new HashMap<>();
                map.put("ip", groupCountJson.getString("key"));
                map.put("counts", groupCountJson.getString("doc_count"));
                map.put("s_unit_id", aptdetail.getString("s_unit_id"));
                map.put("s_area_id", aptdetail.getString("s_area_id"));
                map.put("d_unit_id", aptdetail.getString("d_unit_id"));
                map.put("d_area_id", aptdetail.getString("d_area_id"));
                map.put("d_ip", aptdetail.getString("dip"));
                map.put("s_ip", aptdetail.getString("sip"));
                map.put("unit_id", aptdetail.getString("dvcuid"));
                map.put("area_id", aptdetail.getString("dvcaid"));
                map.put("types", aptdetail.getString("types"));
                map.put("attack_grade", aptdetail.getString("grade"));
                map.put("attack_type", aptdetail.getString("types"));
                aptList.add(map);
            });
        }


        IPage<Map<String, Object>> mapIPage = new Page<>();
//        //处理错误参数的异常
        boolean flag = dataType != null && (dataType == 0 || dataType == 1 || dataType == 2 || dataType == 3 ||
                dataType == 4 || dataType == 5 || dataType == 6 || dataType == 7);
        mapIPage.setRecords(flag ? aptList : new ArrayList<>());
        mapIPage.setSize(10);
        mapIPage.setCurrent(1);
        mapIPage.setPages(1);
        mapIPage.setTotal(10);
        if (!mapIPage.getRecords().isEmpty()) {
            int i = 0;
            for (Map map : mapIPage.getRecords()) {
//                String ip = dataType != null && dataType.equals(3) ? (String) map.get("d_ip") : (String) map.get("ip");

                String ip = "";
                if (dataType != null && dataType == 3) {
                    ip = (String) map.get("d_ip");
                } else {
                    ip = (String) map.get("ip");
                }


                System.err.println("ip:" + ip);
                JSONObject asset = assetComponent.getAssetByIp(ip);
                String deviceName = asset == null || asset.getString("name") == null ? "" : asset.getString("name");

                map.put("device_name", deviceName);

                if (searchSIp) {
                    map.put("department", netStructComponent.getUnitName(Integer.parseInt((String) map.get("s_unit_id"))));
                    map.put("unit", netStructComponent.getUnitName(Integer.parseInt((String) map.get("s_unit_id"))));
                    map.put("area", netStructComponent.getAreaName(Integer.parseInt((String) map.get("s_area_id"))));
                }
                if (searchDIp) {
                    map.put("department", netStructComponent.getUnitName(Integer.parseInt((String) map.get("d_unit_id"))));
                    map.put("unit", netStructComponent.getUnitName(Integer.parseInt((String) map.get("d_unit_id"))));
                    map.put("area", netStructComponent.getAreaName(Integer.parseInt((String) map.get("d_area_id"))));
                }
                if (!searchDIp && !searchSIp) {
                    map.put("department", netStructComponent.getUnitName(Integer.parseInt((String) map.get("unit_id"))));
                    map.put("unit", netStructComponent.getUnitName(Integer.parseInt((String) map.get("unit_id"))));
                    map.put("area", netStructComponent.getAreaName(Integer.parseInt((String) map.get("area_id"))));
                }

                map.put("index", ++i);
            }
        }

        List<Map<String, Object>> list = mapIPage.getRecords();
        return list;
    }


    public BoolQueryBuilder generateTrendPublicQueryBuilder(Integer dataType,
                                                            List<Integer> areaIdList,
                                                            List<Integer> unitIdList,
                                                            String deviceType) {
        //搜索条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        if (dataType != null && (dataType == 1 || dataType == 0 || dataType == 5)) {//内网主动攻击
//            QueryBuilder queryBuilder = inNetService.queryWrapperForSipIn();
//            boolQueryBuilder.must(queryBuilder);
//        }
//
//        if (dataType != null && (dataType == 3 || dataType == 6)) {//外网ip
//            QueryBuilder queryBuilder = inNetService.queryWrapperForSipOut();
//            boolQueryBuilder.must(queryBuilder);
//        }

        if (dataType != null && dataType == 1) {//主动攻击(sip为内网)
            boolQueryBuilder.must(QueryBuilders.termQuery("scode.keyword", ""));


        }

        if (dataType != null && dataType == 2) {//被攻击（dip为内网）
            boolQueryBuilder.must(QueryBuilders.termQuery("dcode.keyword", ""));
        }

        if (dataType != null && dataType == 3) {//外网攻击 (sip为外网)
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("scode.keyword", ""));
        }


        final boolean searchSIp = dataType == null || dataType == 1;
        final boolean searchDIp = dataType == null || dataType == 2;

        if (!searchSIp && !searchDIp) {
            QueryBuilder destinationQueryBuilder = QueryBuilders.boolQuery().should(QueryBuilders.termsQuery("s_area_id", areaIdList))
                    .should(QueryBuilders.termsQuery("s_unit_id", unitIdList))
                    .should(QueryBuilders.termsQuery("d_area_id", areaIdList))
                    .should(QueryBuilders.termsQuery("d_unit_id", unitIdList));

            boolQueryBuilder.must(destinationQueryBuilder);

        }


        if (areaIdList != null && searchSIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (areaIdList != null && searchDIp) {

            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_area_id", areaIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchSIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("s_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }
        if (unitIdList != null && searchDIp) {
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("d_unit_id", unitIdList);
            boolQueryBuilder.must(queryBuilder);
        }


//        List<String> deviceIps = (List<String>) iAssetService.getIpListByDeviceType(deviceType).getData();
//        if (searchSIp && searchDIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.boolQuery()
//                        .should(QueryBuilders.termsQuery("sip", deviceIps))
//                        .should(QueryBuilders.termsQuery("dip", deviceIps));
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchSIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.termsQuery("sip", deviceIps);
//                boolQueryBuilder.must(queryBuilder);
//            }
//        } else if (searchDIp) {
//            if (deviceIps != null) {
//                QueryBuilder queryBuilder = QueryBuilders.termsQuery("dip", deviceIps);
//                boolQueryBuilder.must(queryBuilder);
//            }
//        }

        return boolQueryBuilder;
    }


    public JSONArray getRiskClassTypeCount(List<Integer> areaIdList,
                                           List<Integer> unitIdList,
                                           TimeParam timeParam) {

        JSONArray resultArray = new JSONArray();

        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoolQueryBuilder boolQueryBuilder = generateTrendPublicQueryBuilder(0, areaIdList, unitIdList, "");
        boolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                .gte(df.format(timeParam.getStartTime()))
                .lte(df.format(timeParam.getEndTime())));

        TermsAggregationBuilder aggregationBuilder1 = AggregationBuilders.terms("group_by_risk_class").field("risk_class.keyword");

        TermsAggregationBuilder aggregationBuilder2 = AggregationBuilders.terms("group_by_sip").field("sip.keyword");
        TermsAggregationBuilder aggregationBuilder3 = AggregationBuilders.terms("group_by_types").field("types.keyword");
        aggregationBuilder1.subAggregation(aggregationBuilder2);
        aggregationBuilder1.subAggregation(aggregationBuilder3);
        aggregationBuilder1.size(10000);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.size(0);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregationBuilder1);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.err.println(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject aggObject = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");

        JSONObject aggDetailObject = aggObject.getJSONObject("sterms#group_by_risk_class");

        if (aggDetailObject != null) {

            JSONArray bucketArray = aggDetailObject.getJSONArray("buckets");

            if (bucketArray.size() != 0) {
                for (Object obj : bucketArray) {
                    JSONObject riskClassObject = new JSONObject();
                    riskClassObject.put("riskClass", ((JSONObject) obj).getString("key"));
                    riskClassObject.put("types", ((JSONObject) obj).getJSONObject("sterms#group_by_sip").getJSONArray("buckets"));
                    riskClassObject.put("typeArray", ((JSONObject) obj).getJSONObject("sterms#group_by_types").getJSONArray("buckets"));
                    riskClassObject.put("counts", ((JSONObject) obj).getString("doc_count"));
                    resultArray.add(riskClassObject);
                }
            }

        }

        return resultArray;
    }

    /**
     * 获取每个Grade层级的Ip数量
     *
     * @param dataType
     * @param areaIdList
     * @param unitIdList
     * @param timeParam
     * @return
     */
    public Map<String, Integer> sipCountMap(Integer dataType,
                                            List<Integer> areaIdList,
                                            List<Integer> unitIdList,
                                            TimeParam timeParam) {

        Map<String, Integer> dataMap = new LinkedHashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        SearchRequest searchRequest1 = new SearchRequest("intrusion_result");
        BoolQueryBuilder lowBoolQueryBuilder = generateTrendPublicQueryBuilder(dataType, areaIdList, unitIdList, "");

        lowBoolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_LOW));
        lowBoolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                .gte(df.format(timeParam.getStartTime()))
                .lte(df.format(timeParam.getEndTime())));

        TermsAggregationBuilder lowAggregationBuilder = AggregationBuilders.terms("group_by_sip").field("sip.keyword");
        lowAggregationBuilder.size(100000);
        SearchSourceBuilder sourceBuilder1 = new SearchSourceBuilder();
        sourceBuilder1.query(lowBoolQueryBuilder);
        sourceBuilder1.aggregation(lowAggregationBuilder);
        sourceBuilder1.size(0);
        searchRequest1.source(sourceBuilder1);
        SearchResponse lowResponse = null;
        try {
            lowResponse = restHighLevelClient.search(searchRequest1, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        int low = 0;
        JSONObject aggObject1 = (JSONObject.parseObject(lowResponse.toString())).getJSONObject("aggregations");
        JSONObject aggDetailObject1 = aggObject1.getJSONObject("sterms#group_by_sip");
        if (aggDetailObject1 != null) {
            JSONArray bucketArray1 = aggDetailObject1.getJSONArray("buckets");
            low = bucketArray1.size();
        }
        dataMap.put("lowIpCount", low);


        SearchRequest searchRequest2 = new SearchRequest("intrusion_result");
        BoolQueryBuilder midBoolQueryBuilder = generateTrendPublicQueryBuilder(dataType, areaIdList, unitIdList, "");
        midBoolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_MID));
        midBoolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                .gte(df.format(timeParam.getStartTime()))
                .lte(df.format(timeParam.getEndTime())));
        TermsAggregationBuilder middleAggregationBuilder = AggregationBuilders.terms("group_by_sip").field("sip.keyword");
        SearchSourceBuilder sourceBuilder2 = new SearchSourceBuilder();
        sourceBuilder2.size(0);
        sourceBuilder2.query(midBoolQueryBuilder);
        sourceBuilder2.aggregation(middleAggregationBuilder);
        searchRequest2.source(sourceBuilder2);
        SearchResponse middleResponse = null;
        try {
            middleResponse = restHighLevelClient.search(searchRequest2, RequestOptions.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
        int middle = 0;
        JSONObject aggObject2 = (JSONObject.parseObject(middleResponse.toString())).getJSONObject("aggregations");
        JSONObject aggDetailObject2 = aggObject2.getJSONObject("sterms#group_by_sip");
        if (aggDetailObject2 != null) {
            JSONArray bucketArray2 = aggDetailObject2.getJSONArray("buckets");
            middle = bucketArray2.size();
        }
        dataMap.put("middleIpCount", middle);


        SearchRequest searchRequest3 = new SearchRequest("intrusion_result");
        BoolQueryBuilder highBoolQueryBuilder = generateTrendPublicQueryBuilder(dataType, areaIdList, unitIdList, "");
        highBoolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_HIGH));
        highBoolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                .gte(df.format(timeParam.getStartTime()))
                .lte(df.format(timeParam.getEndTime())));

        TermsAggregationBuilder highAggregationBuilder = AggregationBuilders.terms("group_by_sip").field("sip.keyword");
        SearchSourceBuilder sourceBuilder3 = new SearchSourceBuilder();
        sourceBuilder3.size(0);
        sourceBuilder3.query(highBoolQueryBuilder);
        sourceBuilder3.aggregation(highAggregationBuilder);
        searchRequest3.source(sourceBuilder3);
        SearchResponse highResponse = null;
        try {
            highResponse = restHighLevelClient.search(searchRequest3, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int high = 0;
        JSONObject aggObject3 = (JSONObject.parseObject(highResponse.toString())).getJSONObject("aggregations");
        JSONObject aggDetailObject3 = aggObject3.getJSONObject("sterms#group_by_sip");
        if (aggDetailObject3 != null) {
            JSONArray bucketArray3 = aggDetailObject3.getJSONArray("buckets");
            high = bucketArray3.size();
        }
        dataMap.put("highIpCount", high);
        return dataMap;
    }


    /**
     * 根据s_unit_id和sip获取单位入侵数据（主动攻击）
     * @param areaIdList
     * @param unitIdList
     * @param timeParam
     * @return
     */
    public List<Map<String, Object>> getAptDataByUnit(List<Integer> areaIdList,
                                                      List<Integer> unitIdList,
                                                      TimeParam timeParam) {


        List<Map<String, Object>> dataList = new LinkedList<>();
        //扫描时间段
        String period = "近期";
        switch (timeParam.getTimeType()) {
            case 4:
                period = "今天";
                break;
            case 5:
                period = "本周";
                break;
            case 6:
                period = "本月";
                break;
            case 0:
                period = "近期";
                break;
            default:
                break;
        }

        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoolQueryBuilder boolQueryBuilder = generateTrendPublicQueryBuilder(1, areaIdList, unitIdList, "");
        boolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                .gte(df.format(timeParam.getStartTime()))
                .lte(df.format(timeParam.getEndTime())));

        boolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_HIGH));
        TermsAggregationBuilder aggregationBuilder1 = AggregationBuilders.terms("group_by_s_unit_id").field("s_unit_id");

        TermsAggregationBuilder aggregationBuilder2 = AggregationBuilders.terms("group_by_sip").field("sip.keyword");
        aggregationBuilder2.subAggregation(AggregationBuilders.topHits("apt_detail").size(1));
        TermsAggregationBuilder aggregationBuilder3 = AggregationBuilders.terms("group_by_types").field("risk_sort.keyword");

        aggregationBuilder1.subAggregation(aggregationBuilder2);
        aggregationBuilder1.subAggregation(aggregationBuilder3);
        aggregationBuilder1.size(10000);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.size(0);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregationBuilder1);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.err.println(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject aggObject = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");

        JSONObject aggDetailObject = aggObject.getJSONObject("lterms#group_by_s_unit_id");

        System.err.println(aggDetailObject);

        if (aggDetailObject != null) {

            JSONArray bucketArray = aggDetailObject.getJSONArray("buckets");
            if (bucketArray.size() != 0) {
                int index = 1;
                for (Object obj : bucketArray) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("index", index);
                    map.put("unit_id",((JSONObject) obj).getString("key") );
                    map.put("high_count", ((JSONObject) obj).getString("doc_count"));
                    map.put("period", period);

                    String unitName = netStructComponent.getUnitName(((JSONObject) obj).getInteger("key"));

                    map.put("unit", unitName);

                    JSONArray sipArray = ((JSONObject) obj).getJSONObject("sterms#group_by_sip").getJSONArray("buckets");
                    JSONArray typesArray = ((JSONObject) obj).getJSONObject("sterms#group_by_types").getJSONArray("buckets");

                    map.put("high_sip_count", sipArray.size());

                    StringBuilder riskClasses = new StringBuilder();
                    if(typesArray.size()>3){

                        riskClasses.append(((JSONObject) typesArray.get(0)).getString("key"));
                        riskClasses.append("、");
                        riskClasses.append(((JSONObject) typesArray.get(1)).getString("key"));
                        riskClasses.append("、");
                        riskClasses.append(((JSONObject) typesArray.get(2)).getString("key"));
                        riskClasses.append("、");
                    }else{
                        for (Object typesObj : typesArray) {
                            riskClasses.append(((JSONObject) typesObj).getString("key"));
                            riskClasses.append("、");
                        }
                    }

                    String riskClassesStr = riskClasses.toString();
                    if (riskClasses.toString().startsWith("、")) {
                        riskClassesStr = riskClasses.substring(1, riskClasses.length());
                    }

                    map.put("risk_class", riskClassesStr.substring(0, riskClassesStr.length() - 1));

                    //todo...
                    map.put("risk_class_description", "");

                    List<Map<String, Object>> dataTable = new LinkedList<>();
                    for (Object sipObj : sipArray) {
                        Map<String, Object> tableMap = new HashMap<>();
                        tableMap.put("unit", unitName);
                        tableMap.put("ip", ((JSONObject) sipObj).getString("key"));
                        tableMap.put("counts", ((JSONObject) sipObj).getString("doc_count"));
                        tableMap.put("grade", "高");

                        JSONObject hitObject = ((JSONObject) sipObj).getJSONObject("top_hits#apt_detail")
                                .getJSONObject("hits").getJSONArray("hits").getJSONObject(0);

                        if (hitObject != null) {
                            tableMap.put("types", hitObject.getJSONObject("_source").getString("types"));
                            tableMap.put("area", netStructComponent.getAreaName(hitObject.getJSONObject("_source").getInteger("s_area_id")));
                        }

                        //备注...暂时不知内容
                        tableMap.put("remark", "");

                        dataTable.add(tableMap);
                    }

                    map.put("table_data", dataTable);

                    dataList.add(map);
                    index++;
                }

            }

        }

        System.err.println("result：" + dataList);
        return dataList;
    }


    public List<Map<String, Object>> getAptTableDataByUnit(
            List<Integer> areaIdList,
            List<Integer> unitIdList,
            TimeParam timeParam) {

        List<Map<String, Object>> dataList = new LinkedList<>();
        //扫描时间段
        String period = "近期";
        switch (timeParam.getTimeType()) {
            case 4:
                period = "今天";
                break;
            case 5:
                period = "本周";
                break;
            case 6:
                period = "本月";
                break;
            case 0:
                period = "近期";
                break;
            default:
                break;
        }

        SearchRequest searchRequest = new SearchRequest("intrusion_result");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BoolQueryBuilder boolQueryBuilder = generateTrendPublicQueryBuilder(1, areaIdList, unitIdList, "");
        boolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
                .gte(df.format(timeParam.getStartTime()))
                .lte(df.format(timeParam.getEndTime())));

        boolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_HIGH));
        TermsAggregationBuilder aggregationBuilder1 = AggregationBuilders.terms("group_by_s_unit_id").field("s_unit_id");

        TermsAggregationBuilder aggregationBuilder2 = AggregationBuilders.terms("group_by_s_area_id").field("s_area_id");

        TermsAggregationBuilder aggregationBuilder3 = AggregationBuilders.terms("group_by_sip").field("sip.keyword");

        aggregationBuilder2.subAggregation(aggregationBuilder3);
        aggregationBuilder1.subAggregation(aggregationBuilder2);
        aggregationBuilder1.size(10000);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.size(0);
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.aggregation(aggregationBuilder1);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            System.err.println(searchRequest);
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.err.println(searchResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject aggObject = (JSONObject.parseObject(searchResponse.toString())).getJSONObject("aggregations");

        JSONObject aggDetailObject = aggObject.getJSONObject("lterms#group_by_s_unit_id");

        System.err.println(aggDetailObject);

        if (aggDetailObject != null) {

            JSONArray bucketArray = aggDetailObject.getJSONArray("buckets");
            if (bucketArray.size() != 0) {
                for (Object obj : bucketArray) {
                    Map<String, Object> map = new HashMap<>();
                    String unitName = netStructComponent.getUnitName(((JSONObject) obj).getInteger("key"));

                    map.put("unit", unitName);

                    JSONArray areaArray = ((JSONObject) obj).getJSONObject("lterms#group_by_s_area_id").getJSONArray("buckets");

                    List<Map<String, Object>> dataTable = new LinkedList<>();
                    for (Object areaObj : areaArray) {
                        Map<String, Object> tableMap = new HashMap<>();
                        tableMap.put("counts", ((JSONObject) areaObj).getString("doc_count"));

                        tableMap.put("area", netStructComponent.getAreaName(((JSONObject) areaObj).getInteger("key")));

                        JSONArray sipArray = ((JSONObject) areaObj).getJSONObject("sterms#group_by_sip").getJSONArray("buckets");

                        tableMap.put("high_sip_count", sipArray.size());

                        dataTable.add(tableMap);
                    }
                    map.put("table_data", dataTable);
                    dataList.add(map);
                }

            }

        }

        System.err.println("result：" + dataList);
        return dataList;
    }


    public String testElasticSearch() {

        if (true) {

            TimeParam timeParam = new TimeParam();
//            timeParam.setStartTime(LocalDateTime.of(2020, 04, 23, 15, 23, 12));
//            timeParam.setEndTime(LocalDateTime.of(2020, 06, 23, 15, 23, 12));
            timeParam.setTimeType(5);

//            List<Integer> areaList = netStructComponent.areaGetChildren(96);
//            List<Integer> unitList = netStructComponent.unitGetChildren(1);

//            List<Integer> areaList = roleAreaComponent.roleAreaList(1, 1);
//            List<Integer> unitList = roleUnitComponent.roleUnitList(1, 1);

            List<Integer> areaList = new ArrayList<Integer>() {{
                add(232);
            }};

            List<Integer> unitList = new ArrayList<Integer>() {{
                add(386);
            }};

            System.err.println(areaList);
            System.err.println(unitList);

            System.err.println("StartTime: " + timeParam.getStartTime());

            System.err.println("EndTime: " + timeParam.getEndTime());

            List<Map<String, Object>> topData = getTop(1, areaList, unitList, null, "", timeParam);
            System.err.println("top list size: " + topData.size());
            topData.forEach(map -> {
                System.err.println(map);

            });



            return "OK";
        }

//        CountRequest countRequest = new CountRequest("intrusion_result");
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//
//        boolQueryBuilder.must(QueryBuilders.termQuery("grade", Apt.ATTACK_STAGE_HIGH));
//        boolQueryBuilder.must(QueryBuilders.rangeQuery("happen_time")
//                .lte("2020-06-23 15:23:12")
//                .gte("2020-05-25 15:23:12"));
//        countRequest.query(boolQueryBuilder);
//        CountResponse highResponse = null;
//        try {
//            highResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Integer high = Math.toIntExact(highResponse.getCount());

        return "OK";
    }

    @Autowired
    private RoleAreaComponent roleAreaComponent;

    @Autowired
    private RoleUnitComponent roleUnitComponent;

}
