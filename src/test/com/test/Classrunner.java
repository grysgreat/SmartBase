package com.test;

import com.itranswarp.compiler.JavaStringCompiler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.*;
import com.star.classana.gsonhelp.FieldInfo;
import com.star.classana.gsonhelp.LocalDataTypeAdapter;
import com.star.classana.gsonhelp.MeteInfo;
import org.junit.Test;

/**
 * 针对动态生成类的测试类
 */
public class Classrunner {
    public Classrunner(){};
    /**
     * 给Gson注册一个LocalData类的解析器
     */
    private Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .registerTypeAdapter(LocalDate.class,new LocalDataTypeAdapter())
            .create();

    /**
     * 原先的动态类加载demo
     * @param source
     * @return
     * @throws Exception
     */
    public Object run(String source) throws Exception {

// 声明类名
        String className = "Student";
        String packageName = "com.atguigu.mytestcalss";
// 声明包名：package top.fomeiherz;
        String prefix = String.format("package %s;", packageName);
// 全类名：top.fomeiherz.Main
        String fullName = String.format("%s.%s", packageName, className);
// 编译器
        JavaStringCompiler compiler = new JavaStringCompiler();
// 编译：compiler.compile("Main.java", source)
        Map results = compiler.compile(className + ".java", prefix + source);
// 加载内存中byte到Class>对象
        Class clazz = compiler.loadClass(fullName, results);
// 创建实例
        Object instance = clazz.newInstance();
        //Method mainMethod = clazz.getMethod("Student");
        Constructor cs = clazz.getConstructor(null);
        String jsonStr ="{" +
                "\"id\": 1,\n" +
                "\"name\": \"xiaoming\","+
                "\"birth\":\"2012-06-01\""+
                "}";
        return this.gson.fromJson(jsonStr,clazz);
// String[]数组时必须使用Object[]封装
// 否则会报错：java.lang.IllegalArgumentException: wrong number of arguments

    }

    @Test// 编译java字符串并生成对象
    public  void runaobject() throws Exception {
        // 传入String类型的代码
        String sourcse = "import java.time.LocalDate;\n" +
                "/**\n" +
                " * table name:  student\n" +
                " * author name: FelixChan\n" +
                " * create time: 2022-05-03 00:47:47\n" +
                " */ \n" +
                "public class Student {\n" +
                "\n" +
                "\tprivate int id;\n" +
                "\tprivate String name;\n" +
                "\tprivate LocalDate birth;\n" +
                "\n" +
                "\tpublic Student() {\n" +
                "\t\tthis.name=\"name\";this.id=0;this.birth = LocalDate.now(); \n" +
                "\t}\n" +
                "\tpublic Student(int id,String name,LocalDate birth) {\n" +
                "\t\tthis.id=id;\n" +
                "\t\tthis.name=name;\n" +
                "\t\tthis.birth=birth;\n" +
                "\t}\n" +
                "\tpublic void setId(int id){\n" +
                "\t\tthis.id=id;\n" +
                "\t}\n" +
                "\tpublic int getId(){\n" +
                "\t\treturn id;\n" +
                "\t}\n" +
                "\tpublic void setName(String name){\n" +
                "\t\tthis.name=name;\n" +
                "\t}\n" +
                "\tpublic String getName(){\n" +
                "\t\treturn name;\n" +
                "\t}\n" +
                "\tpublic void setBirth(LocalDate birth){\n" +
                "\t\tthis.birth=birth;\n" +
                "\t}\n" +
                "\tpublic LocalDate getBirth(){\n" +
                "\t\treturn birth;\n" +
                "\t}\n" +
                "\t@Override\n" +
                "\tpublic String toString() {\n" +
                "\t\treturn \"student[\" + \n" +
                "\t\t\t\"id=\" + id + \n" +
                "\t\t\t\", name=\" + name + \n" +
                "\t\t\t\", birth=\" + birth + \n" +
                "\t\t\t\"]\";\n" +
                "\t}\n" +
                "\tpublic String getPrimaryKey() {\n" +
                "\t\treturn \"id\";\n" +
                "\t}\n" +
                "}\n" +
                "\n";

        Classrunner cl  = new Classrunner()   ;
        Object studenttest= cl.run(sourcse);//调用上面的run方法
        System.out.println(studenttest);
    }

    @Test//通过metaInfo类调用来生成类
    public void testMete2String() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        MeteInfo meteInfo = new MeteInfo();
        meteInfo.setName("student");
        List<FieldInfo> fieldlist= new ArrayList<>();
        fieldlist.add(new FieldInfo("id","int"));
        fieldlist.add(new FieldInfo("name","varchar"));
        fieldlist.add(new FieldInfo("birth","date"));
        meteInfo.setColumn(fieldlist);
        String javastring = meteInfo.Convert2JavaObject();
        System.out.println(javastring);
        Class classtemp = meteInfo.compilerMeta();
        //Object sudenttemp = classtemp.newInstance();
        String jsonStr ="{" +
                "\"id\": 1,\n" +
                "\"name\": \"xiaoming\","+
                "\"birth\":\"2012-06-01\""+
                "}";
        Object sudenttemp=this.gson.fromJson(jsonStr,classtemp);//通过生成的class 利用Gson生成对象
        System.out.println(sudenttemp);
    }

    @Test//从json开始生成meteinfo 改json从接口http://localhost:8082/DataBaseConfigToTable/ChangeToTable/{id}}生成
    public void testfromjson() throws IOException, ClassNotFoundException {
        String json = "{\"name\":\"student\",\"sqlType\":null,\"column\":[{\"name\":\"id\",\"typeName\":\"INT\",\"type\":4,\"pk\":true},{\"name\":\"name\",\"typeName\":\"VARCHAR\",\"type\":12,\"pk\":false},{\"name\":\"birth\",\"typeName\":\"DATE\",\"type\":91,\"pk\":false}],\"count\":2}";

        MeteInfo meteInfo = MeteInfo.BuildfromJSOn(json);
        Class classtemp = meteInfo.compilerMeta();
        String jsonStr ="{" +
                "\"id\": 1,\n" +
                "\"name\": \"xiaoming\","+
                "\"birth\":\"2012-06-01\""+
                "}";
        Object sudenttemp=this.gson.fromJson(jsonStr,classtemp);//通过生成的class 利用Gson生成动态对象
        System.out.println(sudenttemp);
    }
}
