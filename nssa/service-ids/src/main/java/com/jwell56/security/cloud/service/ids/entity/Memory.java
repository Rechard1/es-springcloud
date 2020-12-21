package com.jwell56.security.cloud.service.ids.entity;

import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/3
 */
@Data
public class Memory {
    private Double phy_totalUser;//GB
    private Double phy_total;
    private Double phy_used;
    private Double phy_lave;
    private Double vir_totalUser;
    private Double vir_total;
    private Double vir_used;
    private Double vir_lave;
}
