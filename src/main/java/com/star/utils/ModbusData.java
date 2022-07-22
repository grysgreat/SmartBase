package com.star.utils;

import lombok.Data;

@Data
public class ModbusData {
    private  Integer id;//标识id 与ModbusConfig对应
    Integer type; // 1 2 3 4
    Integer slave_id;//
    Integer offset;//0-9
    Integer datatype;
}
