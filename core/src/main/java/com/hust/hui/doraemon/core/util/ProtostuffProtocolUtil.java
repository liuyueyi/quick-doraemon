package com.hust.hui.doraemon.core.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by yihui on 2017/11/28.
 */
@Slf4j
public class ProtostuffProtocolUtil {

    public static byte[] toByteForObject(String key, Object obj) {
        if (null == obj) {
            return null;
        }
        Schema schema = RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return bytes;
        } catch (Exception e) {
            log.error("Protostuff序列化失败", e);
            return null;
        } finally {
            buffer.clear();
        }
    }

    public static <T> byte[] toByteForObjectList(String key, List<T> objList, Class<T> clz) {
        if (objList == null || objList.isEmpty()) {
            return null;
        }
        Schema<T> schema = RuntimeSchema.getSchema(clz);
        LinkedBuffer buffer = LinkedBuffer.allocate(8192);
        byte[] protostuff = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
            protostuff = bos.toByteArray();
        } catch (Exception e) {
            log.error("序列化列表失败", e);

        } finally {
            buffer.clear();
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                log.error("close buffer error.", e);
            }
        }

        return protostuff;
    }

    public static <T> T toObject(String key, byte[] bytes, Class<T> clazz) {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        Schema schema = RuntimeSchema.getSchema(clazz);
        try {
            T object = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(bytes, object, schema);
            return object;
        } catch (InstantiationException e) {
            log.error("反序列化失败", e);
        } catch (IllegalAccessException e) {
            log.error("反序列化失败", e);
        }

        return null;
    }

    public static <T> List<T> toObjectList(String key, byte[] bytes, Class<T> clazz) {
        if (null == bytes || bytes.length == 0) {
            return null;
        }

        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        List<T> result = null;
        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(bytes), schema);
        } catch (IOException e) {
            log.error("反序列化列表失败", e);
        }
        return result;
    }
}
