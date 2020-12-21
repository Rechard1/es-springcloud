package com.jwell56.security.cloud.service.asset.entity.other;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/2
 */
@Data
@TableName("bs-core-flow-*")
public class Flow extends IdsBaseInfo {

    @ApiModelProperty(value = "接收流量", notes = "info", readOnly = true)
    public Integer flow__bytes_toclient;

    @ApiModelProperty(value = "发送流量", notes = "info", readOnly = true)
    public Integer flow__bytes_toserver;

    @ApiModelProperty(value = "接收数据包", notes = "info", readOnly = true)
    public Integer flow__pkts_toclient;

    @ApiModelProperty(value = "发送数据包", notes = "info", readOnly = true)
    public Integer flow__pkts_toserver;
}
