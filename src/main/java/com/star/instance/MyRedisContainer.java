package com.star.instance;

import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyRedisContainer implements MyRedisCommandsContainer,Cloneable{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MyRedisContainer.class);
    private final JedisPool jedisPool;
    private final JedisSentinelPool jedisSentinelPool;

    public MyRedisContainer(JedisPool jedisPool) {
        Preconditions.checkNotNull(jedisPool, "Jedis Pool can not be null");
        this.jedisPool = jedisPool;
        this.jedisSentinelPool = null;
    }

    public MyRedisContainer(JedisSentinelPool sentinelPool) {
        Preconditions.checkNotNull(sentinelPool, "Jedis Sentinel Pool can not be null");
        this.jedisPool = null;
        this.jedisSentinelPool = sentinelPool;
    }


    @Override
    public Map<String,String> hget(String key) {
        Jedis jedis = null;
        try {
            jedis = this.getInstance();
            Map<String,String> map = new HashMap<String,String>();
            Set<String> fieldSet = jedis.hkeys(key);
            for(String s : fieldSet){
                map.put(s,jedis.hget(key,s));
            }
            return  map;
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Cannot get Redis message with command HGET to key {} error message {}", new Object[]{key, e.getMessage()});
            }
            throw e;
        } finally {
            this.releaseInstance(jedis);
        }
    }

    private Jedis getInstance() {
        return this.jedisSentinelPool != null ? this.jedisSentinelPool.getResource() : this.jedisPool.getResource();
    }

    private void releaseInstance(Jedis jedis) {
        if (jedis != null) {
            try {
                jedis.close();
            } catch (Exception var3) {
                LOG.error("Failed to close (return) instance to pool", var3);
            }

        }
    }

    public void close() {
        if (this.jedisPool != null) {
            this.jedisPool.close();
        }

        if (this.jedisSentinelPool != null) {
            this.jedisSentinelPool.close();
        }

    }
}
