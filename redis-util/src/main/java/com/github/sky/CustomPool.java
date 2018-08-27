package com.github.sky;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

public class CustomPool {

    private JedisPool jedisPool;
    private JedisSentinelPool jedisSentinelPool;
    private boolean isSentinel;

    public CustomPool(JedisPool jedisPool){
        this.isSentinel = false;
        this.jedisPool = jedisPool;
    }

    public CustomPool(JedisSentinelPool jedisSentinelPool){
        this.isSentinel = true;
        this.jedisSentinelPool = jedisSentinelPool;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public JedisSentinelPool getJedisSentinelPool() {
        return jedisSentinelPool;
    }

    public void setJedisSentinelPool(JedisSentinelPool jedisSentinelPool) {
        this.jedisSentinelPool = jedisSentinelPool;
    }

    public boolean isSentinel() {
        return isSentinel;
    }

    public void setSentinel(boolean sentinel) {
        isSentinel = sentinel;
    }

    public Jedis getResource(){
        if(isSentinel){
            return jedisSentinelPool.getResource();
        }
        return jedisPool.getResource();
    }

}
