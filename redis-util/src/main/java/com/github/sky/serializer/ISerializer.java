package com.github.sky.serializer;

/**
 * Description：序列化接口
 * Author: sukai
 * Date: 2017-08-11
 */
public interface ISerializer {

    // key的序列化与反序列化
    byte[] keyToBytes(String key);
    String keyFromBytes(byte[] bytes);

    // field的序列化与反序列化
    byte[] fieldToBytes(Object field);
    Object fieldFromBytes(byte[] bytes);

    // value的序列化与反序列化
    byte[] valueToBytes(Object value);
    Object valueFromBytes(byte[] bytes);
}

