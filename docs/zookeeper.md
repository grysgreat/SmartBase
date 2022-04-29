zookeeper配置

- 下载zk- https://zookeeper.apache.org/releases.html

- 文件夹改名

```
mv apache zookeeper 3.5.7 bin/ zookeeper 3.5.7
```

- 配置服务器编号

```
（1）在 /opt/module/zookeeper-3.5.7/这个目录下创建 zkData
mkdir zkData
（2）在 /opt/module/zookeeper-3.5.7/zkData目录下创建一个 myid的文件
vi myid
在文件中添加与server对应的编号 （注意：上下不要有空行，左右不要有空格

（3）拷贝配置好的 zookeeper到其他机器上
xsync zookeeper 3.5.7
并分别在 hadoop103、 hadoop104上 修改 myid文件中内容为 3、 4
```

- 配置zoo.cfg文件

- ```
  （1）重命名 /opt/module/zookeeper-3.5.7/conf这个目录下的 zoo_sample.cfg为 zoo.cfg
  	mv zoo_sample.cfg zoo.cfg
  （2)打开 zoo.cfg文件
  	vim zoo.cfg
  #修改 数据存储路径 配置
  dataDir=/opt/module/zookeeper 3.5.7 /zkData
  
  #增加如下配置
  #######################
  server.2=hadoop102:2888:3888
  server.3=hadoop103:2888:3888
  server.4=hadoop104:2888:3888
  （3） 同步 zoo.cfg配置文件
   xsync zoo.cfg
  ```

- 集群启动脚本配置

  ```
  vim zk.sh
  
  
  添加
  #!/bin/bash
  case $1 in
  "
  for i in hadoop102 hadoop103 hadoop104
  do
  echo zookeeper $i 启动
  ssh $i "/opt/module/zookeeper 3.5.7/bin/zkServer.sh
  start"
  done
  "
  for i in hadoop102 hadoop103 hadoop104
  do
  echo zookeeper $i 停止
  ssh $i "/opt/module/zookeeper 3.5.7/bin/zkServer.sh
  stop"
  done
  "
  for i in hadoop102 hadoop103 hadoop104
  do
  echo zookeeper $i 状态
  ssh $i "/opt/module/zookeeper 3.5.7/bin/zkServer.sh
  status"
  done
  esac
  
  
  增加脚本执行 权限  chmod u+x zk.sh
  
  
  ```

  - Zookeeper集群 启动  zk.sh start       关闭 zk.sh stop  