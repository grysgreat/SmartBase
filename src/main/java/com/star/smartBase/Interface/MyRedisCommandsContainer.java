package com.star.smartBase.Interface;

import java.io.Serializable;
import java.util.Map;

public interface MyRedisCommandsContainer extends Serializable {
    Map<String,String> hget(String key);
    void close();
}
