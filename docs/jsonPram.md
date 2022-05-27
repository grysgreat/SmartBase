**任务json提交说明**

可以将任务流转化为一个json，然后通过提交json实现自定义任务流的提交。json格式与大致写法如下

下面为参数解释：
- jobNum：任务流分为的子流个数;  子流 => 从任务书头单向遍历到尾的一条路径称作为一个子流，由source，dest和中间算子组成
- jobi: 每一个子流为一个类，从 job1到jobN
- source,dest： 源,目的数据类：含有数据源：mysql，redis，hdfs，kafka，port，text(json,csv...)见代码中docs类
    - types：源数据类型
    - url： 源数据url (text，csv等为保存的地址路径)
    - port： 源数据端口
    - topic： 源数据标题（kafka为流的标题，redis为hset的标题）
    - basename：mysql数据库名   
    - tablename: mysql表名
- opreator:算子类，含有算子个数，算子opreator1到opreatorN
    - num：算子个数
    - opreatori：第i个算子类
        - type：算子种类
        - key：算子参数
        - 含有算子如下：   
            - OpCount：流统计算子，统计以，分割的单词个数，返回为二元组(key,num)
            - OpFilt：过滤出含有key的流
            - OpKill：去除含有key的流
            - OpMap：将源数据中字段映射到目标数据中"x1,y1,z1"——为目标数据中第下标个字段对应为源数据中的第几个
            - OpNew：自定义算子：key为"自定义算子jar包地址&自定义算子全类名"
                - 自定义算子写法：在自己的算子项目中添加com.star.instance.MyOpreator.java接口类,代码如下,然后在自己写的算子类中继承这个接口即可.

```java
package com.star.instance;

import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;

public interface MyOprator {
    public SingleOutputStreamOperator<String> getOpOut(SingleOutputStreamOperator<String> steam);
}

```

    
    

```json
{
  "JobNum": 1,  
  "job1": {
    "source": {
      "types": "kafka",
      "url": "hadoop102",
      "port": "9092",
      "topic": "kfkSQL"
    },
    "operator": {
      "num": 1,
      "operator1": {
        "type": "OpNew",
        "key": "file:F:/tmp/smart-base2/BaseHub/target/BaseHub-1.0-SNAPSHOT-jar-with-dependencies.jar&com.star.opretors.transforms.OpCount"
      }
    },
    "dest": {
      "types": "kafka",
      "url": "hadoop102",
      "port": "9092",
      "topic": "kfkPort"
    }
  }
}
```