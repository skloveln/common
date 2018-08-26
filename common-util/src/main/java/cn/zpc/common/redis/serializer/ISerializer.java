package cn.zpc.common.redis.serializer;

/**
 * Description:ISerializer
 * Author: sukai
 * Date: 2017-08-11
 */
public interface ISerializer {

    byte[] keyToBytes(String key);
    String keyFromBytes(byte[] bytes);

    byte[] fieldToBytes(Object field);
    Object fieldFromBytes(byte[] bytes);

    byte[] valueToBytes(Object value);
    Object valueFromBytes(byte[] bytes);
}

