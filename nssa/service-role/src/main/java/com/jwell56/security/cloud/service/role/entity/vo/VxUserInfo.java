package com.jwell56.security.cloud.service.role.entity.vo;

import lombok.Data;

@Data
public class VxUserInfo {
    private String  code;

    private String encryptedData;

    private String iv;
}
