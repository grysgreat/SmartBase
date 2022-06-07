package com.star.classana.gsonhelp;

import com.google.gson.Gson;
import com.itranswarp.compiler.JavaStringCompiler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 通过
 */
public class MeteInfo {

    public String name;
    public List<FieldInfo> column;
    public int count;

    public MeteInfo(){}
    public MeteInfo(String name, List<FieldInfo> column, int count) {
        this.name = name;
        this.column = column;
        this.count = count;
    }
    public static MeteInfo BuildfromJSOn(String json){
        MeteInfo meteInfo;
        meteInfo =new Gson().fromJson(json,MeteInfo.class);
        return meteInfo;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FieldInfo> getColumn() {
        return column;
    }

    public void setColumn(List<FieldInfo> column) {
        this.column = column;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "MeteInfo{" +
                "ClassName='" + name + '\'' +
                ", column=" + column +
                ", columnConut=" + count +
                '}';
    }

    public String Convert2JavaObject(){
        String javaObject ="";
        javaObject+="import java.time.LocalDate;\n";
        javaObject+="public class "+initCap(this.name)+"{\t\n";
        //空构造

        //设置字段
        for (FieldInfo names : this.column){
            javaObject +="public " +sqlType2JavaType(names.getTypeName()) +" "+ names.getName()+";\t\n";
        }

        //生成get/set
        for (FieldInfo names : this.column){
            javaObject +="public "+sqlType2JavaType(names.getTypeName()) +" get" + initCap(names.getName())+"(){\t\n";
            javaObject +="return this."+names.getName()+";\n";
            javaObject +="}\t\n";
        }

        for (FieldInfo names : this.column){
            javaObject +="public void set" + initCap(names.getName())+"("+sqlType2JavaType(names.getTypeName())+" "+names.getName()+"){\t\n";
            javaObject +="\t this."+names.getName()+"="+names.getName()+";\t\n";
            javaObject +="}\t\n";
        }
        //重写toString
        javaObject+="\t@Override\r\n\tpublic String toString() {\r\n return " +
                "\""+this.name +"[\" +";
                for (FieldInfo names : this.column){
                    javaObject+="\""+names.getName()+":"+"\" +"+names.getName()+"+\",\"+";
                }
        javaObject+="\"]\";\t\n}";



        javaObject+="\t\n}";
        return javaObject;
    }

    private String sqlType2JavaType(String sqlType) {
        if (sqlType.equalsIgnoreCase("bit")) {
            return "boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint")) {
            return "byte";
        } else if (sqlType.equalsIgnoreCase("smallint")) {
            return "short";
        } else if (sqlType.equalsIgnoreCase("int")) {
            return "int";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "float";
        } else if (sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "double";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text")|| sqlType.equalsIgnoreCase("longtext")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("timestamp")) {
            return "LocalDateTime";
        }else if (sqlType.equalsIgnoreCase("date")) {
            return "LocalDate";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        }else if (sqlType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        }
        return null;
    }
    /**
     * @param str 传入字符串
     * @return
     * @description 将传入字符串的首字母转成大写
     */
    private String initCap(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z')
            ch[0] = (char) (ch[0] - 32);
        return new String(ch);
    }

    public Class compilerMeta() throws IOException, ClassNotFoundException {
        JavaStringCompiler compiler = new JavaStringCompiler();
        Map results = compiler.compile(initCap(this.name)+ ".java", "package com.ob;"+Convert2JavaObject());
        Class clazz = compiler.loadClass("com.ob."+initCap(this.name), results);
        return clazz;
    }
}
