package com.jwell56.security.cloud.service.asset.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_cnvd_information")
@AllArgsConstructor
@NoArgsConstructor
public class CnvdInformationVo {

	//对应id，可不填
    @TableId(value = "cnvd_information_id")
    private Integer cnvdInformationId;
    
    private String cnvdId;
    
    private String cveId;

    private String grade;

    private String affectProduct;

    private String loopholeDes;

    private String solution;

    private String loopholeType;

    private String type;
    
    private String name;

    private Integer userId;

    private String userName;

    private Integer enterpriseId;

    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private LocalDateTime reportingTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
