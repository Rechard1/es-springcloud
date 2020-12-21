package com.jwell56.security.cloud.service.apt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceTargetInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String type;

    private String areaName;

    private String unitName;

    private String people;
    //海外地区名
    private String continent;
    //海外地区代码
    private String areacode;

    private String country;

    private String province;

    private String city;

    private String wgsLon;

    private String wgsLat;
}
