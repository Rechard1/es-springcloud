package com.jwell56.security.cloud.service.apt.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AptExcel extends BaseRowModel{

    @ExcelProperty(value = "来源ip", index = 1)
    private String sip;

	@ExcelProperty(value = "来源名称", index = 2)
	private String sAssetName;

    @ExcelProperty(value = "来源区域", index = 3)
    private String sAreaName;

    @ExcelProperty(value = "来源单位", index = 4)
    private String sUnitName;

    @ExcelProperty(value = "目的ip", index = 5)
    private String dip;
	
	@ExcelProperty(value = "目的名称", index = 6)
    private String dAssetName;

    @ExcelProperty(value = "目的区域", index = 7)
    private String dAreaName;
    
    @ExcelProperty(value = "目的单位", index = 8)
    private String dUnitName;
    
}
