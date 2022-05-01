 **使用flink-session模式首先进行如下配置** 

配置yarn-hadoop高可用：

cd conf/zoo.cfg

```
添加
# ZooKeeper quorum peers
server.0=hadoop102:2888:3888
```



cd conf/flink-conf.yaml

```
添加
yarn.application-attempts: 3
high-availability: zookeeper
high-availability.storageDir: hdfs://hadoop102:8020/flink/yarn/ha
high-availability.zookeeper.quorum: hadoop102:2181,hadoop103:2181,hadoop104:2181
high-availability.zookeeper.path.root: /flink-yarn

```

xsync分发脚本

```
xsync flink-conf.yaml 
```

启动zookeeper

```
cd flink-1.13.2/bin
./start-zookeeper-quorum.sh
```

```
关闭yarn虚拟内存检测（yarn-session不报错）：
在hadoop01、hadoop02、hadoop03中的yarn-site.xml中配置如下：
<!--关闭nm的虚拟内存检测-->
<property>
         <name>yarn.nodemanager.vmem-check-enabled</name>
        <value>false</value>
</property>
```

