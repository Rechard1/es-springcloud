package com.jwell56.security.cloud.service.apt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AreaUnit {
    private List<Integer> areaIdListRole;
    private List<Integer> unitIdListRole;
    private List<Integer> sAreaIdList;
    private List<Integer> sUnitIdList;
    private List<Integer> dAreaIdList;
    private List<Integer> dUnitIdList;
    private List<Integer> assetAreaIdList;
    private List<Integer> assetUnitIdList;
    private List<Integer> deviceAreaIdList;
    private List<Integer> deviceUnitIdList;
}
