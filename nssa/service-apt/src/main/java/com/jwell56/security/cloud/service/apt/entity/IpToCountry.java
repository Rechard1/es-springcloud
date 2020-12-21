package com.jwell56.security.cloud.service.apt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="安全事件对象")
@TableName("sys_ip_country_full") //对应表名
public class IpToCountry  extends Model<IpToCountry> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type= IdType.AUTO)
    private int id;

    private long minip;

    private long maxip;
    //海外地区名
    private String continent;
    //海外地区代码
    private String areacode;

    private String country;

    private String province;

    private String city;

    private String wgsLon;

    private String wgsLat;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
