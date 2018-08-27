package com.github.sky;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Protocol;

import java.util.HashSet;
import java.util.Set;

public class RedisExecutor {

    private static class RedisHolder{
        private static CustomPool customPool;
        static{
            PropertiesUtil propertiesUtil = new PropertiesUtil("redis.properties");
            String mode = propertiesUtil.getProperty("redis.mode", "standard");
            String password = propertiesUtil.getProperty("redis.password", null);
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            if(mode.equals("standard")){
                String host = propertiesUtil.getProperty("redis.host", "127.0.0.1");
                Integer port = Integer.parseInt(propertiesUtil.getProperty("redis.port", "6379"));
                JedisPool jedisPool = new JedisPool(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, password);
                customPool = new CustomPool(jedisPool);
            }else{
                String masterName = propertiesUtil.getProperty("redis.master.name", "mymaster");
                String sentinels = propertiesUtil.getProperty("redis.sentinels", null);
                Set<String> sentinelSet = new HashSet<>();
                String[] strings = StringUtils.split(sentinels, ",");
                for(String str : strings){
                    sentinelSet.add(str);
                }
                JedisSentinelPool jedisSentinelPool =
                        new JedisSentinelPool(masterName, sentinelSet, poolConfig, Protocol.DEFAULT_TIMEOUT, password);
                customPool = new CustomPool(jedisSentinelPool);
            }
        }
    }

    public static CustomPool getCustomPool(){
        return RedisHolder.customPool;
    }

}
