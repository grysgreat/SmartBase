# BaseHub
![logo](pics/download.png)


 —— 二十二年壬寅五月既望
　　

## 🚀 介绍`
SmartBase的进阶版本，支持更强大的任务处理功能。通过`源`，`目标`，`算子`组合成任意流式数据任务。


## 🍗 软件架构
项目通过linux集群运行。包含三个`flink-core`，`Universal-pipe`，`smartBase-console`核心架构.
- 任务处理架构为`flink-yarn`集群，通过`yarn-session`运行任务，`slot`可以按需使用，提高集群的资源利用率。同时配置高可用，`Job-Manager`可以与`task-manager`宕机时自动转换。
- web项目交互搭建在Springboot框架上，使用kafka负载均衡集群作为任务上传与结果的消息队列。
- logs返回至消息服务器（springboot），传回至前端。
    - [springboot链接](https://gitee.com/StarGrys/smart-base)
- 前端使用Angular框架。
    - [前端链接](https://gitee.com/det0cte/smart-base-web-dashborad)


### 🏳‍🌈 组成部分

#### 1️⃣ flink-core

`flink-core` 定位是一个flink处理框架，关注编码开发，规范了参数，按照约定优于配置的方式进行开发，融合了`DataStream` 和 `Flink sql` api，简化繁琐的操作，聚焦业务本身，提高开发效率和开发体验

#### 2️⃣ Universal-pipe

`pipe` 是水管的意思，`Universal-pipe` 的定位是一个数据抽取的处理集群，基于 `Hadoop`，`Yarn`等对于`flink-core` 中提供的各种，数据源开发，目的是打造一个稳定高效的数据处理终端集群，并且集成到 `smartBase-console` 中，解决实时数据源获取问题。

#### 3️⃣ smartBase-console

`smartBase-console` 是一个综合实时数据平台，低代码(`Low Code`)平台，可以较好的管理`Flink`任务，集成了项目编译、发布、参数配置、启动、`savepoint`，火焰图(`flame graph`——待完成)，`Flink SQL`， 监控等诸多功能于一体，大大简化了 `Flink` 任务的日常操作和维护，融合了诸多最佳实践。其最终目标是打造成一个实时数仓，流批一体的一站式大数据解决方案，该平台使用但不仅限以下技术:

* [Apache Flink](http://flink.apache.org)
* [Apache YARN](http://hadoop.apache.org)
* [Spring Boot](https://spring.io/projects/spring-boot/)
* [Mybatis](http://www.mybatis.org)
* [Mybatis-Plus](http://mp.baomidou.com)
* [Flame Graph](http://www.brendangregg.com/FlameGraphs)
* [JVM-Profiler](https://github.com/uber-common/jvm-profiler)
* [Angular](https://angular.cn/)
* [Ant Design](https://antd.com/)
* [Apifox](https://gitee.com/apifox/)
* ...

![项目总体架构](pics/MetaFlink2.png)


## 🍪 集群配置
-  **环境说明** ： 以下为项目运行所需环境，按以下配置不会出现版本兼容性问题。
    - 集群：`Linux`: CentOS-3.10.0-862.el7.x86_64 * 3
    - `gcc`： 4.8.5 20150623 (Red Hat 4.8.5-44) (GCC) 
    - `java`：1.8.0_212
    - `redis`：6.2.1
    - `kafka`：2.13-3.1.0
    - `hadoop`：3.1.3
    - `flink`：1.13.2
    - `zookeeper`: 3.5.7
    
-  **模块配置** 
    1.  `Linux`，`gcc`，`java` 请自行百度
    2.  [redis文档](docs/redis.md)
    3.  [kafka文档](docs/kafka.md)
    4.  [flink文档](docs/flink.md)
    5.  [mysql文档](docs/mysql.md)


## 🍦 功能概要
-  **目前支持的功能有**
    - 源数据/目标数据：mysql，kafka，redis，port，csv，json
    - 算子：

        1.散算子 ![散算子](pics/OpratorPic/san.png)

        2.合算子 ![合算子](pics/OpratorPic/he.png)

        3.存算子 ![存算子](pics/OpratorPic/cun.png)

        4.灭算子 ![灭算子](pics/OpratorPic/mie.png)

        5.计算子 ![计算子](pics/OpratorPic/ji.png)

        6.时算子 ![时算子](pics/shi.png)
 
        7.映算子 ![映算子](pics/ying.png)
 




## 🍟 使用说明
1.  flink启动说明：
    - 首先启动`hdfs-yarn`集群

```
  cd /home/star/bin
  ./myhadoop start
```

- Yarn模式启动flink集群-session模式——详细配置见：[flink文档](docs/flink.md)

- ```
  ./yarn-session.sh -nm test -d
  ```

2.docker
```
链接: https://pan.baidu.com/s/1Vk0nalAkdWixWvNyKVoMlA?pwd=1x9p 提取码: 1x9p 复制这段内容后打开百度网盘手机App，操作更方便哦 
```


## 🍍 开发者说明
-  **模块配置** 
    1.  [Flink内部传参文档](docs/Parameter.md)
    2.  [ApiFox请求接口文档](docs/interfaceTest.md)
-  **用户自定义模板代码**
    1.  [下载地址](https://gitee.com/StarGrys/smart-base/tree/UserCodes/)


## 🍇 参与贡献

1. 贡献代码
2. 软件功能测试
3. 请我吃薯片
4. 关注**嘉然今天吃什么（bushi！）**

 

## 🔪 特技

1.  老麻抄手吃20个加豆皮加卤蛋
2.  晚上不睡早上不起
3.  英雄联盟艾欧尼亚铂金
4.  桥牌七无将大师(把把硬叫7NT)
5.  15秒速通理塘！

![logo](pics/logo1.png)