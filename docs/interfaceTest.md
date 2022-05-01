# 测试用例

下载apifox软件 



阅读[REST API | Apache Flink](https://nightlies.apache.org/flink/flink-docs-release-1.14/zh/docs/ops/rest_api/#cluster)

按照用例编写接口请求 可以先按照类型随便写

因为都是REST API 所以只设置好Params 就可以了

不需要进行请求

注意

-  在这里加入新接口
  - ![image-20220501160817920](../pics/test1.png)

-  在url中有：** 的格式 如**/jars/:jarid/run **这里的：jarid 需要在下方的path中设定
  - ![image-20220501160535723](../pics/test2.png)
-  注意请求的类型（在文档中给有标注）
  - ![image-20220501160744554](../pics/test3.png)

- 对于怎么写请求

- 以这个为例

- ```
  {
    "type" : "object",
    "id" : "urn:jsonschema:org:apache:flink:runtime:webmonitor:handlers:JarRunRequestBody",
    "properties" : {
      "allowNonRestoredState" : {
        "type" : "boolean"
      },
      "entryClass" : {
        "type" : "string"
      },
      "jobId" : {
        "type" : "any"
      },
      "parallelism" : {
        "type" : "integer"
      },
      "programArgs" : {
        "type" : "string"
      },
      "programArgsList" : {
        "type" : "array",
        "items" : {
          "type" : "string"
        }
      },
      "savepointPath" : {
        "type" : "string"
      }
    }
  }            
       
  ```

![image-20220501161118170](../pics/test6.jpg)

并不是从以上的代码中获取相关的Parameter值
注意从上图中进行编写
其中
- Verb 为请求方式 图中为 GET
- Path Parameters 为请求字段 按对应apifox中的 Path参数
- Query Parameters  为请求字段 对应apifox中的 Query参数