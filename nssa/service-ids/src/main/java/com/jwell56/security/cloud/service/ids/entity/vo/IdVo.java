package com.jwell56.security.cloud.service.ids.entity.vo;

import com.jwell56.security.cloud.service.ids.annotation.Validation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/11
 */
@Data
public class IdVo {
    @ApiModelProperty(value = "od")
    @Validation(allowEmpty = false)
    private String id;
}
