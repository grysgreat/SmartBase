# 关于参数传递
## 帮助类
详情见 com/star/smartBase/utils/ParameterHelper.java

## FLink 内传参
在main函数中进行转化
```java

       Configuration conf = new Configuration();
        conf.setString("mykey","myvalue");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(conf);

```
形成key-vale

然后在算子中可以获取 如在MysqlSink中
```java
    @Override
    public void open(Configuration parameters) throws Exception {
        // TODO Auto-generated method stub
        super.open(parameters);
        //获取全局变量 也就是
        ExecutionConfig.GlobalJobParameters globalParams = getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
        Configuration globConf = (Configuration) globalParams;
        System.out.println( globConf.getString("mykey", null));

    }
```

需要再open中进行
    - 读取相关参数的操作
    - 处理SQL表结构 动态生成sql 难点在于SET的不同方法 可参考**

