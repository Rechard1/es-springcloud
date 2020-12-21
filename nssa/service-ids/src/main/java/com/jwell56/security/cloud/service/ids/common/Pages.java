package com.jwell56.security.cloud.service.ids.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2019/8/30
 */
@Data
public class Pages {
    private static final int DEFAULT_PAGE_SIZE = 10;

    @ApiModelProperty(example = "10", value = "页长，默认为10")
    private Integer pageSize;

    @ApiModelProperty(example = "1", value = "页码，默认为1")
    private Integer pageNum;
}
