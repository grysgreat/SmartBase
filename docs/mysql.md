 **mysql说明** 

- mysql安装 - 请自行百度

-  **mysql为该项目中的一个数据流动环节，使用时应注意以下事项** 

- mysql开启binlog

  ```
  修改mysql的my.cnf配置文件
  一般默认是在/etc/my.cnf路径下
  
  简单开启binlog demo
  在mysqld下添加第一种方式
  
  #第一种方式:
  #开启binlog日志
  log_bin=ON
  #binlog日志的基本文件名
  log_bin_basename=/var/lib/mysql/mysql-bin
  #binlog文件的索引文件，管理所有binlog文件
  log_bin_index=/var/lib/mysql/mysql-bin.index
  #配置serverid
  server-id=1
  
  
  修改完之后重启mysql
  ```

  

- 设置时区为GMT

- 设置允许远程登录

  ```
  在装有MySQL的机器上登录MySQL mysql -u root -p密码
  执行use mysql;
  执行update user set host = '%' where user = 'root';这一句执行完可能会报错，不用管它。
  执行FLUSH PRIVILEGES;
  ```

- 保存检查点

  ```
  !!!在8081运行flink时一定要加-m
  
  bin/flink savepoint -m hadoop102:8020 84040beca43d0893b20eb89e83991e7e(任务id) hdfs://hadoop102:8020/rng/savepoint（保存路径）
  ```

- 重启任务

  ```
  加-s参数
  
  bin/flink run -m hadoop102:8081 
  -s hdfs://hadoop102:8020/rng/savepoint/savepoint-84040b-730344135ecc 
  -c com.atguigu.FlinkCDC ./gmall-flink-cdc-1.0-SNAPSHOT-jar-with-dependencies.jar
  ```

  

