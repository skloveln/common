package com.github.sky.serializer;

import com.github.sky.utils.LogUtils;
import redis.clients.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Description:JdkSerializer
 * Author: sukai
 * Date: 2017-08-11
 */
public class JdkSerializer implements ISerializer {

    public static final ISerializer jdkSerializer = new JdkSerializer();

    public byte[] keyToBytes(String key) {
        return SafeEncoder.encode(key);
    }

    public String keyFromBytes(byte[] bytes) {
        return SafeEncoder.encode(bytes);
    }

    public byte[] fieldToBytes(Object field) {
        return valueToBytes(field);
    }

    public Object fieldFromBytes(byte[] bytes) {
        return valueFromBytes(bytes);
    }

    public byte[] valueToBytes(Object value) {
        ObjectOutputStream objectOut = null;
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            objectOut = new ObjectOutputStream(bytesOut);
            objectOut.writeObject(value);
            objectOut.flush();
            return bytesOut.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(objectOut != null)
                try {objectOut.close();} catch (Exception e) {
                    LogUtils.error(e.getMessage(), e);}
        }
    }

    public Object valueFromBytes(byte[] bytes) {
        if(bytes == null || bytes.length == 0)
            return null;

        ObjectInputStream objectInput = null;
        try {
            ByteArrayInputStream bytesInput = new ByteArrayInputStream(bytes);
            objectInput = new ObjectInputStream(bytesInput);
            return objectInput.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (objectInput != null)
                try {objectInput.close();} catch (Exception e) {
                    LogUtils.error(e.getMessage(), e);}
        }
    }
}
