package com.jwell56.security.cloud.service.netstruct.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wsg
 * @since 2019/11/1
 */
@Data
public class SearchSqlDto {
    private List<Integer> areaIdList;
    private List<Integer> unitIdList;
    private String field;
    private Boolean isNum;
}
