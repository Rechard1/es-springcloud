package com.jwell56.security.cloud.service.netstruct.entity.commons;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageParam<T> {
    @ApiModelProperty(example = "10", value = "页长，默认为10")
    private Integer pageSize;

    @ApiModelProperty(example = "1", value = "页码，默认为1")
    private Integer pageNum;

    public IPage<T> iPage() {
        return new Page<>(pageNum == null ? 1 : pageNum, pageSize == null ? 10 : pageSize);
    }
}
