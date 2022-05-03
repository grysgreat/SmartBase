

- 首先进入目录  cd /usr/local/bin/

  - 然后使用配置文件打开redis

    ```
    redis-server myredis.conf
    ```

- 打开redis 

- ```
  redis-cli
  ```

- 查看keys：keys *

- 查看所有内容

- ```
  hgetall "keys"
  ```

- 

- 启动：redis-server    /opt/redis-stable/redis.conf

关闭：redis-cli shutdown 或者 kill redis进程的pid