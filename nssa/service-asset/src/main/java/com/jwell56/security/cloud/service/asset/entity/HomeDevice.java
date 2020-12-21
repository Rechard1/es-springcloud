package com.jwell56.security.cloud.service.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class HomeDevice implements Serializable {
    //台数
    private int counts;
    //数据量
    private int sum;

    private int devicez;

    private int devicel;
}
