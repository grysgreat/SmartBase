package com.star.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.io.Serializable;

@Data  // 注解在类上，为类提供读写属性，还提供equals()、hashCode()、toString()方法
@AllArgsConstructor  // 注解在类上，为类提供全参构造函数，参数的顺序与属性定义的顺序一致
@NoArgsConstructor  // 注解在类上，为类提供无参构造函数
public class Student implements Serializable {
    private String GoodName;
    private BufferedImage GoodPic;

    @Override
    public String toString() {
        return "Student{" +
                "GoodName='" + GoodName + '\'' +
                ", GoodPic=" + GoodPic +
                '}';
    }
}