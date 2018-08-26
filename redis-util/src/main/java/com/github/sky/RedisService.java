package com.github.sky;

import com.github.sky.serializer.FstSerializer;
import com.github.sky.serializer.ISerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

/**
 * Description:Redis插件
 * Author: sukai
 * Date: 2017-08-11
 */
@Service
public class RedisService extends Cache{

    @Autowired
    public RedisService(JedisPool jedisPool) {
        super("main-cache", jedisPool, FstSerializer.fstSerializer, IKeyNamingPolicy.defaultKeyNamingPolicy);
        Redis.addCache(this);
    }

    public RedisService(JedisPool jedisPool, ISerializer serializer, IKeyNamingPolicy keyNamingPolicy){
        super("main-cache", jedisPool, serializer, keyNamingPolicy);
        Redis.addCache(this);
    }

}


