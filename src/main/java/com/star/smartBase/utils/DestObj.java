package com.star.smartBase.utils;

import lombok.Data;

import java.util.Arrays;
import java.util.Map;

@Data
public class DestObj {
    private String destName;
    private Map<String,String> destInfo;
    private String[] data;
}
