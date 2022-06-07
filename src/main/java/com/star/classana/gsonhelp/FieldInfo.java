package com.star.classana.gsonhelp;

/**
 * metaInfo中的字段信息
 */
public class FieldInfo {
    public String name  ;
    public String typeName;

    public FieldInfo(){}
    public FieldInfo(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "FieldInfo{" +
                "name='" + name + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
