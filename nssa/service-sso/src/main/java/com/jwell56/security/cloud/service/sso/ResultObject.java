package com.jwell56.security.cloud.service.sso;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(value = "统一返回对象")
public class ResultObject implements Serializable {

    @ApiModelProperty(value = "返回的消息")
    private String msg;

    @ApiModelProperty(value = "返回的状态码")
    private Integer code;

    @ApiModelProperty(value = "返回成功或者失败标志")
    private Boolean success;

    @ApiModelProperty(value = "返回数据集合")
    private Object data;
}
