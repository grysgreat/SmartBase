package com.star.instance;


import lombok.Data;

import java.io.Serializable;

@Data
public class OpratorsPram implements Serializable {
    private String opType;
    private String key;

    public OpratorsPram(String opType, String key) {
        this.opType = opType;
        this.key = key;
    }
}
