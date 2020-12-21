package com.jwell56.security.cloud.common;

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
public class ResultObject<T> implements Serializable {

    @ApiModelProperty(value = "返回的消息")
    private String msg;

    @ApiModelProperty(value = "返回的状态码")
    private Integer code;

    @ApiModelProperty(value = "返回成功或者失败标志")
    private Boolean success;

    @ApiModelProperty(value = "返回数据集合")
    private T data;

    public ResultObject(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public ResultObject(String msg, Integer code, Boolean success) {
        this.msg = msg;
        this.code = code;
        this.success = success;
    }

    public static <T> ResultObject<T> data(T data, String message) {
        ResultObject<T> resultObject = new ResultObject<>();
        resultObject.setMsg(message);
        resultObject.setData(data);
        resultObject.setSuccess(true);
        resultObject.setCode(HttpServletResponse.SC_OK);
        return resultObject;
    }

    public static <T> ResultObject<T> data(T data) {
        return ResultObject.data(data, "");
    }

    public static ResultObject<Map<String, String>> value(String key, String value) {
        ResultObject<Map<String, String>> resultObject = new ResultObject<>();
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(key, value);
        resultObject.setMsg("");
        resultObject.setData(resultMap);
        resultObject.setSuccess(true);
        resultObject.setCode(HttpServletResponse.SC_OK);
        return resultObject;
    }

    public static ResultObject exception(Exception e) {
        ResultObject resultObject = new ResultObject<>();
        e.printStackTrace();
        resultObject.setMsg(e.getMessage());
        resultObject.setSuccess(false);
        resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return resultObject;
    }

    public void msg(String msg, boolean success) {
        if (success) {
            this.setMsg(msg);
            this.setSuccess(true);
            this.setCode(HttpServletResponse.SC_OK);
        } else {
            this.setMsg(msg);
            this.setSuccess(false);
            this.setCode(HttpServletResponse.SC_BAD_REQUEST);
        }
    }


    public static ResultObject message(String msg, boolean success) {
        if (success) {
            return message(msg + "成功");
        } else {
            return badRequest(msg + "失败");
        }
    }

    public static ResultObject message(String msg) {
        ResultObject resultObject = new ResultObject<>();
        resultObject.setMsg(msg);
        resultObject.setSuccess(true);
        resultObject.setCode(HttpServletResponse.SC_OK);
        return resultObject;
    }

    public static ResultObject badRequest(String msg) {
        ResultObject resultObject = new ResultObject<>();
        resultObject.setMsg(msg);
        resultObject.setSuccess(false);
        resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
        return resultObject;
    }
}
