package com.jwell56.security.cloud.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsg
 * @since 2019-05-15
 */
@Data
@AllArgsConstructor
public class ExcelItem implements Serializable {
    private List<String> headList;
    private List<List<String>> dataList;
    private String sheetName;
    private Integer sheetIndex;

    public ExcelItem(){
        this.headList=new ArrayList<>();
        this.dataList=new ArrayList<>();
        this.sheetName="";
        this.sheetIndex=0;
    }
}
