package com.jwell56.security.cloud.common.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wsg
 * @since 2019/8/28
 */
@Data
public class HttpObject {
    private Integer responseCode;
    private String body;
    private Map<String, String> header;
    private String cookie;
    private String exceptionMsg;

    HttpObject() {
        this.responseCode = 0;
        this.header = new HashMap<>();
        this.body = "";
        this.cookie = "";
        this.exceptionMsg = "";
    }
}
