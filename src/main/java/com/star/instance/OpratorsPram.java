package com.star.instance;


import lombok.Data;

@Data
public class OpratorsPram {
    private String opType;
    private String key;

    public OpratorsPram(String opType, String key) {
        this.opType = opType;
        this.key = key;
    }
}
