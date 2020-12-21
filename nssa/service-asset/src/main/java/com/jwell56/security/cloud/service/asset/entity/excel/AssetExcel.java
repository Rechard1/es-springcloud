package com.jwell56.security.cloud.service.asset.entity.excel;

import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetExcel extends BaseRowModel{

	@ExcelProperty(value = "时间", index = 0)
	private LocalDateTime createTime;
	
	@ExcelProperty(value = "ip", index = 1)
	private String ip;
    
	@ExcelProperty(value = "资产名称", index = 2)
	private String name;
	
	@ExcelProperty(value = "资产类型", index = 3)
	private String type;
	
	@ExcelProperty(value = "区域", index = 4)
    private String areaName;
	
	@ExcelProperty(value = "单位", index = 5)
    private String unitName;
    
    @ExcelProperty(value = "是否为重要资产", index = 6)
    private String importantType;
    
    @ExcelProperty(value = "MAC", index = 7)
    private String mac;
    
    @ExcelProperty(value = "负责人", index = 8)
    private String principal;
    
    @ExcelProperty(value = "负责人电话", index = 9)
    private String principalPhone;
    
    @ExcelProperty(value = "物理位置", index = 10)
    private String phyAddress;
    
    @ExcelProperty(value = "扩展信息", index = 11)
    private String expandInfo;
}
