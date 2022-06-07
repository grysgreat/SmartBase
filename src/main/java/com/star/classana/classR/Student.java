package com.star.classana.classR;
/**
 * 这里用来测试生成的java对象是否符合java语法规则
 */

import java.time.LocalDate;
public class Student{
    public int id;
    public String name;
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public void setId(int id){
        this.id=id;
    }
    public void setName(String name){
        this.name=name;
    }
    @Override
    public String toString() {
        return "student[" +"id:" +id+","+"name:" +name+","+"]";
    }
}
