package com.jwell56.security.cloud.service.apt;

import com.alibaba.fastjson.JSONArray;
import com.jwell56.security.cloud.service.apt.entity.TimeParam;
import com.jwell56.security.cloud.service.apt.service.feign.IRoleService;
import com.jwell56.security.cloud.service.apt.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.apt.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.apt.service.serviceImpl.ESAptService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AptStatisticTest {

    @Autowired
    private ESAptService esAptService;

    @Autowired
    private RoleAreaComponent roleAreaService;

    @Autowired
    private RoleUnitComponent roleUnitService;

    @Test
    public void testStatistic(){
        esAptService.testElasticSearch();
    }

    private List<Integer> areaList = new ArrayList<>();
    private List<Integer> unitList = new ArrayList<>();

    @Before
    public void setList(){
//        areaList=roleAreaService.roleAreaList(1, 1);
//        unitList = roleUnitService.roleUnitList(1, 1);
        areaList=roleAreaService.roleAreaList(0, 22);
        unitList = roleUnitService.roleUnitList(0, 22);

    }


    @Test
    public void testAptES(){
        TimeParam timeParam = new TimeParam();
        timeParam.setTimeType(6);

        JSONArray array = esAptService.getRiskClassTypeCount(areaList, unitList, timeParam);
        System.err.println(array);

        /**
         *
         * 结果示例：
         * [
         *   {
         *     "types": [
         *       {
         *         "doc_count": 47490,
         *         "key": "远程控制"
         *       },
         *       {
         *         "doc_count": 31353,
         *         "key": "DOS_ICMP_FLOOD_拒绝服务"
         *       },
         *       {
         *         "doc_count": 25194,
         *         "key": "弱口令"
         *       },
         *       {
         *         "doc_count": 23648,
         *         "key": "SMB远程溢出攻击"
         *       },
         *       {
         *         "doc_count": 15173,
         *         "key": "IDS规则"
         *       },
         *       {
         *         "doc_count": 10823,
         *         "key": "WEB特征检测"
         *       },
         *       {
         *         "doc_count": 8383,
         *         "key": "DNS response of a queried malware Command and Control domain"
         *       },
         *       {
         *         "doc_count": 4397,
         *         "key": "暴力破解"
         *       },
         *       {
         *         "doc_count": 3552,
         *         "key": "HTTP_url躲避"
         *       },
         *       {
         *         "doc_count": 3248,
         *         "key": "ICMP_Cybercop_扫描"
         *       }
         *     ],
         *     "riskClass": ""
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 25074,
         *         "key": "远程控制"
         *       },
         *       {
         *         "doc_count": 4555,
         *         "key": "SMB远程溢出攻击"
         *       },
         *       {
         *         "doc_count": 4138,
         *         "key": "弱口令"
         *       },
         *       {
         *         "doc_count": 2623,
         *         "key": "WEB特征检测"
         *       },
         *       {
         *         "doc_count": 1573,
         *         "key": "IDS规则"
         *       },
         *       {
         *         "doc_count": 564,
         *         "key": "ICMP_SolarWinds_IP扫描"
         *       },
         *       {
         *         "doc_count": 163,
         *         "key": "系统告警"
         *       },
         *       {
         *         "doc_count": 81,
         *         "key": "DGA域名请求"
         *       },
         *       {
         *         "doc_count": 71,
         *         "key": "Successful logon - RDP"
         *       },
         *       {
         *         "doc_count": 68,
         *         "key": "Archive Upload"
         *       }
         *     ],
         *     "riskClass": "B1"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 21700,
         *         "key": "SMB远程溢出攻击"
         *       },
         *       {
         *         "doc_count": 7833,
         *         "key": "IDS规则"
         *       },
         *       {
         *         "doc_count": 4289,
         *         "key": "远程控制"
         *       },
         *       {
         *         "doc_count": 445,
         *         "key": "暴力破解"
         *       },
         *       {
         *         "doc_count": 98,
         *         "key": "弱口令"
         *       },
         *       {
         *         "doc_count": 32,
         *         "key": "SCAN_UDP端口扫描"
         *       },
         *       {
         *         "doc_count": 15,
         *         "key": "W97M_MARKER.BO - HTTP (Response)"
         *       },
         *       {
         *         "doc_count": 11,
         *         "key": "非法数据传输"
         *       },
         *       {
         *         "doc_count": 7,
         *         "key": "Unregistered service"
         *       },
         *       {
         *         "doc_count": 5,
         *         "key": "自定义特征检测"
         *       }
         *     ],
         *     "riskClass": "US"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 16195,
         *         "key": "非法数据传输"
         *       },
         *       {
         *         "doc_count": 5971,
         *         "key": "弱口令"
         *       },
         *       {
         *         "doc_count": 1692,
         *         "key": "SCAN_UDP端口扫描"
         *       },
         *       {
         *         "doc_count": 667,
         *         "key": "WEB特征检测"
         *       },
         *       {
         *         "doc_count": 379,
         *         "key": "IDS规则"
         *       },
         *       {
         *         "doc_count": 37,
         *         "key": "恶意文件攻击"
         *       },
         *       {
         *         "doc_count": 16,
         *         "key": "DOS_UDP_FLOOD_拒绝服务"
         *       },
         *       {
         *         "doc_count": 16,
         *         "key": "类型为测试"
         *       },
         *       {
         *         "doc_count": 12,
         *         "key": "HTTP_Apache_畸形Range选项处理拒绝服务漏洞利用[CVE-2011-3192]"
         *       },
         *       {
         *         "doc_count": 12,
         *         "key": "HTTP_Microsoft_Windows_TCP/IP_QOS远程拒绝服务漏洞[MS11-064][CVE-2011-1965]"
         *       }
         *     ],
         *     "riskClass": "CN"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 93,
         *         "key": "TCP_后门_Win32.Virut.IrcBot_控制命令"
         *       }
         *     ],
         *     "riskClass": "PL"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 8,
         *         "key": "WHOIS_用户"
         *       }
         *     ],
         *     "riskClass": "GB"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 4,
         *         "key": "WEB特征检测"
         *       },
         *       {
         *         "doc_count": 1,
         *         "key": "SCAN_UDP端口扫描"
         *       },
         *       {
         *         "doc_count": 1,
         *         "key": "非法数据传输"
         *       }
         *     ],
         *     "riskClass": "CA"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 4,
         *         "key": "WHOIS_用户"
         *       },
         *       {
         *         "doc_count": 1,
         *         "key": "非法数据传输"
         *       }
         *     ],
         *     "riskClass": "AU"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 5,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "CV"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 5,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "SE"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 4,
         *         "key": "SCAN_UDP端口扫描"
         *       }
         *     ],
         *     "riskClass": "IT"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 2,
         *         "key": "WEB特征检测"
         *       },
         *       {
         *         "doc_count": 2,
         *         "key": "自定义特征检测"
         *       }
         *     ],
         *     "riskClass": "ZA"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 2,
         *         "key": "自定义特征检测"
         *       }
         *     ],
         *     "riskClass": "BG"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 2,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "LV"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "CO"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "非法数据传输"
         *       }
         *     ],
         *     "riskClass": "IR"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "KR"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "非法数据传输"
         *       }
         *     ],
         *     "riskClass": "MD"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "非法数据传输"
         *       }
         *     ],
         *     "riskClass": "NL"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "RU"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "WEB特征检测"
         *       }
         *     ],
         *     "riskClass": "SC"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "非法数据传输"
         *       }
         *     ],
         *     "riskClass": "SG"
         *   },
         *   {
         *     "types": [
         *       {
         *         "doc_count": 1,
         *         "key": "HTTP_/etc/passwd_访问"
         *       }
         *     ],
         *     "riskClass": "TZ"
         *   }
         * ]
         *
         */

    }



    @Test
    public void testGradeCount(){
        TimeParam timeParam = new TimeParam();
        timeParam.setTimeType(4);

//        Map<String, Integer> data = esAptService.sipCountMap(0, areaList, unitList, timeParam);
//        System.err.println(data);

        List<Map<String, Object>> data = esAptService.getAptDataByUnit(areaList, unitList, timeParam);
//        List<Map<String, Object>> data = esAptService.getAptTableDataByUnit(null, null, timeParam);


        data.forEach(map->{
            System.err.println(map);

        });
    }


    @Test
    public void testTrend() {
        TimeParam timeParam = new TimeParam();
        timeParam.setTimeType(6);

//        List<Map<String, Integer>> dataList = esAptService.aptTrendStatistic(1, areaList, unitList, "", timeParam);
//        dataList.forEach(map->{
//            System.err.println(map);
//        });


        Integer count = esAptService.getHistoryCount(0, areaList, unitList, timeParam.hisStart(), timeParam.hisEnd(), "", "");
        System.err.println(count);

    }


}
