package com.jwell56.security.cloud.service.ids.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.ids.annotation.EntitySortOrder;
import com.jwell56.security.cloud.service.ids.annotation.TimeSearch;
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
public class Trends extends Pies {

    @ApiModelProperty(value = "时间分段")
    private Integer parts;

    public Integer getParts() {
        return parts == null ? 10 : parts;
    }
}
