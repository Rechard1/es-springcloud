package com.jwell56.security.cloud.service.ids.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.ids.annotation.Validation;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author wsg
 * @since 2020/12/3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pies {
    @ApiModelProperty(value = "统计字段")
    @Validation(allowEmpty = false)
    private String field;

    @ApiModelProperty(value = "数据分类数量")
    private Integer counts;

    public Integer getCounts() {
        return counts == null ? 10 : counts;
    }
}
