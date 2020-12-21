package com.jwell56.security.cloud.service.asset.utils;

import com.jwell56.security.cloud.common.entity.Risk;
import com.jwell56.security.cloud.service.asset.service.RiskService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//启动时外边程序不能打开excel
@Component
public class ReadExcel {
    @Autowired
    private RiskService riskService;

//    @PostConstruct
    public void insertData() throws IOException, InvalidFormatException {
        File xlsFile = new File("D:\\project\\nssa.cloud.document\\数据标准文档\\各厂商数据标准\\亚信TDA\\攻击类型.xls");
        // 工作表
        Workbook workbook = WorkbookFactory.create(xlsFile);
        // 表个数。
        int numberOfSheets = workbook.getNumberOfSheets();
        // 遍历表。
        Map<String,String> map = new HashMap<>();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 行数。
            int rowNumbers = sheet.getLastRowNum() + 1;
            // Excel第一行。
            Row temp = sheet.getRow(0);
            if (temp == null) {
                continue;
            }
            int cells = temp.getPhysicalNumberOfCells();
            // 读数据。 不要第一行
            List<Risk> list = new ArrayList<>();
            for (int row = 1; row < rowNumbers; row++) {
                Row r = sheet.getRow(row);

                map.put(r.getCell(0).toString(),r.getCell(1).toString());
//                String name = r.getCell(1).toString();
//                String grade = r.getCell(6).toString();
//                switch (grade){
//                    case "低危": grade = "低";break;
//                    case "中危": grade = "中";break;
//                    case "非攻击": grade = "低";break;
//                    case "高危": grade = "高";break;
//                }
//                String des = "";
//                if(r.getCell(3) != null){
//                    des = r.getCell(3).toString();
//                }
//                System.out.println("id="+r.getCell(0).toString());
//                String suggest = "";
//                if(r.getCell(4) != null){
//                    suggest = r.getCell(4).toString();
//                }
//
//                System.out.println("INSERT INTO `sys_risk`(`risk_type`, `risk_name`, `risk_des`, `risk_suggestion`,`risk_grade`) VALUES " +
//                        "('启明知识库','"+name+"','"+des+"','"+suggest+"','"+grade+"');\n");
//
//                Risk risk = new Risk();
//                risk.setRiskType("启明知识库");
//                risk.setRiskName(name);
//                risk.setRiskGrade(grade);
//                risk.setRiskDes(des);
//                risk.setRiskSuggestion(suggest);
//
//                list.add(risk);
            }
//
//            riskService.saveBatch(list);
        }
        insert(map);
    }

    public void insert(Map<String,String> map) throws IOException, InvalidFormatException {
        List<String> anheng = readFile("D:\\project\\nssa.cloud.document\\数据标准文档\\各厂商数据标准\\安恒APT\\安恒.txt");
        List<String> yaxin = readFile("D:\\project\\nssa.cloud.document\\数据标准文档\\各厂商数据标准\\亚信TDA\\TDA.txt");


        File xlsFile = new File("D:\\project\\nssa.cloud.document\\数据标准文档\\各厂商数据标准\\鞍钢.xls");
        // 工作表
        Workbook workbook = WorkbookFactory.create(xlsFile);
        // 表个数。
        int numberOfSheets = workbook.getNumberOfSheets();
        // 遍历表。
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 行数。
            int rowNumbers = sheet.getLastRowNum() + 1;
            // Excel第一行。
            Row temp = sheet.getRow(0);
            if (temp == null) {
                continue;
            }
            int cells = temp.getPhysicalNumberOfCells();
            // 读数据。 不要第一行row为1
            List<Risk> list = new ArrayList<>();
            for (int row = 0; row < rowNumbers; row++) {
                Row r = sheet.getRow(row);

                String name = r.getCell(0).toString();

                String type="";
                if(anheng.contains(name)){
                    type = "安恒知识库";
                }
                if(yaxin.contains(name)){
                    type = "亚信TDA知识库";
                }
                if(type.equals("")){
                    continue;
                }
                String grade = "中";
                grade = map.get(name);
                if(grade==null){
                    grade = "中";
                }
//                String grade = r.getCell(6).toString();
//                switch (grade){
//                    case "低危": grade = "低";break;
//                    case "中危": grade = "中";break;
//                    case "非攻击": grade = "低";break;
//                    case "高危": grade = "高";break;
//                }
                String des = "";
                if(r.getCell(1) != null) des = r.getCell(1).toString();
                String suggest = "";
                if(r.getCell(2) != null) suggest = r.getCell(2).toString();

                System.out.println("INSERT INTO `sys_risk`(`risk_type`, `risk_name`, `risk_des`, `risk_suggestion`,`risk_grade`) VALUES " +
                        "('TDA知识库','"+name+"','"+des+"','"+suggest+"','"+grade+"');\n");

                Risk risk = new Risk();
                risk.setRiskType(type);
                risk.setRiskName(name);
                risk.setRiskGrade(grade);
                risk.setRiskDes(des);
                risk.setRiskSuggestion(suggest);

                list.add(risk);
            }

            riskService.saveBatch(list);
        }
    }

//    @PostConstruct
    public void insert() throws IOException, InvalidFormatException {
        File xlsFile = new File("D:\\project\\nssa.cloud.document\\数据标准文档\\各厂商数据标准\\病毒库.xls");
        // 工作表
        Workbook workbook = WorkbookFactory.create(xlsFile);
        // 表个数。
        int numberOfSheets = workbook.getNumberOfSheets();
        // 遍历表。
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            // 行数。
            int rowNumbers = sheet.getLastRowNum() + 1;
            // Excel第一行。
            Row temp = sheet.getRow(0);
            if (temp == null) {
                continue;
            }
            int cells = temp.getPhysicalNumberOfCells();
            // 读数据。 不要第一行row为1
            List<Risk> list = new ArrayList<>();
            for (int row = 0; row < rowNumbers; row++) {
                Row r = sheet.getRow(row);

                String name = r.getCell(0).toString();
                String grade = "中";
//                String grade = r.getCell(6).toString();
//                switch (grade){
//                    case "低危": grade = "低";break;
//                    case "中危": grade = "中";break;
//                    case "非攻击": grade = "低";break;
//                    case "高危": grade = "高";break;
//                }
                String des = "";
                if (r.getCell(1) != null) des = r.getCell(1).toString();
                String suggest = "";
                if (r.getCell(2) != null) suggest = r.getCell(2).toString();

                System.out.println("INSERT INTO `sys_risk`(`risk_type`, `risk_name`, `risk_des`, `risk_suggestion`,`risk_grade`) VALUES " +
                        "('病毒库','" + name + "','" + des + "','" + suggest + "','" + grade + "');\n");

                Risk risk = new Risk();
                risk.setRiskType("病毒库");
                risk.setRiskName(name);
                risk.setRiskGrade(grade);
                risk.setRiskDes(des);
                risk.setRiskSuggestion(suggest);

                list.add(risk);
            }

            riskService.saveBatch(list);
        }
    }

    public List<String> readFile(String fileName) throws IOException {
        List<String> list = new ArrayList<>();
        FileInputStream fis=new FileInputStream(fileName);
        System.out.println(Charset.defaultCharset());
        InputStreamReader isr=new InputStreamReader(fis, "GBK");
        BufferedReader br = new BufferedReader(isr);
        String line="";
        while ((line=br.readLine())!=null) {
            list.add(line);
        }
        br.close();
        isr.close();
        fis.close();

        return list;
    }
}
