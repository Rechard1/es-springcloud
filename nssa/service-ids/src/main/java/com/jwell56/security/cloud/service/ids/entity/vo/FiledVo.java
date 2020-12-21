package com.jwell56.security.cloud.service.ids.entity.vo;

import lombok.Data;

@Data
public class FiledVo {

    private boolean value;

    private String label;

    public FiledVo(String label, boolean value) {
        this.label = label;
        this.value = value;
    }

    public FiledVo() {
    }

}
