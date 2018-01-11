package com.hust.hui.doraemon.cache;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yihui on 2017/11/28.
 */
@Getter
@Setter
public class CacheWrapper {
    private Logger logger = LoggerFactory.getLogger(CacheWrapper.class);

    private JedisPool masterJedis;
    private List<JedisPool> slaveJedisList;

    private String masterConf;
    private String slaveConf;
    private int maxIdle;
    private int minIdle;
    private int maxTotal;
    private int timeout;
    private int database;

    private class ConfAddress {
        private String ip;
        private int port;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public ConfAddress(String conf) {
            init(conf);
        }

        private void init(String conf) {
            if (!StringUtils.contains(conf, ":")) {
                return;
            }

            String[] pair = StringUtils.split(conf, ":");
            if (pair == null || pair.length != 2) {
                return;
            }

            this.ip = pair[0];
            this.port = Integer.parseInt(pair[1]);
        }

        public boolean isIllegal() {
            return StringUtils.isBlank(ip) || port <= 0;
        }
    }


    @PostConstruct
    public void init() {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal <= 0 ? 300 : maxTotal);
        config.setMaxIdle(maxIdle <= 0 ? 10 : maxIdle);
        config.setMinIdle(minIdle <= 0 ? 3 : minIdle);
        config.setMaxWaitMillis(timeout <= 0 ? 1000 : timeout);
        config.setTestOnBorrow(false);


        // init master jedis
        ConfAddress masterAddr = new ConfAddress(masterConf);
        if (masterAddr.isIllegal()) {
            throw new JedisException("master jedis conf is error!");
        }
        masterJedis = new JedisPool(config, masterAddr.getIp(), masterAddr.getPort(), this.timeout, null, this.database);


        // init slave jedis
        String[] slaveConfs = StringUtils.split(slaveConf, ",");
        if (slaveConfs == null || slaveConfs.length == 0) {
            slaveJedisList = Collections.emptyList();
        }
        slaveJedisList = new ArrayList<>(slaveConfs.length);
        ConfAddress slaveTmpAddr;
        for (String conf : slaveConfs) {
            slaveTmpAddr = new ConfAddress(conf);
            if (slaveTmpAddr.isIllegal()) {
                continue;
            }
            JedisPool slaveJedis = new JedisPool(config, slaveTmpAddr.getIp(), slaveTmpAddr.getPort(),
                    this.timeout, null, this.database);
            slaveJedisList.add(slaveJedis);
        }
    }


    final int MASTER_JEDIS = 0;
    final int SLAVE_JEIDS = 1;
    // 保证线程安全的自动计数器
    private AtomicInteger chooseCounter = new AtomicInteger();

    /**
     * 获取使用的jedis,这里采用标准的一主多备模式
     *
     * @param type
     * @return
     */
    public JedisPool getJedisPool(int type) {
        if (type == MASTER_JEDIS) {
            return masterJedis;
        }

        if (CollectionUtils.isEmpty(slaveJedisList)) {
            return masterJedis;
        }


        final int chooseIndex = this.chooseCounter.incrementAndGet();
        final int index = chooseIndex % slaveJedisList.size();
        return slaveJedisList.get(index);
    }


    public String get(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key is null!");
        }

        Jedis jedis = null;
        JedisPool pool = getJedisPool(SLAVE_JEIDS);
        try {
            jedis = pool.getResource();
            String ans = jedis.get(key);
            return ans;
        } catch (Exception e) {
            logger.error("get string from cache error! key:{}, e:{}", key, e);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean set(String key, String value) {
        return set(key, value, 0);
    }


    public boolean set(String key, String value, int expire) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value) || expire < 0) {
            throw new IllegalArgumentException("key || value || expire are illegal");
        }

        Jedis jedis = null;
        JedisPool pool = getJedisPool(MASTER_JEDIS);
        String ans;
        try {
            jedis = pool.getResource();
            if (expire > 0) {
                ans = jedis.setex(key, expire, value);
            } else {
                ans = jedis.set(key, value);
            }
        } catch (Exception e) {
            logger.error("set string into cache error! key:{}, value:{}, expire:{}, e:{}", key, value, expire, e);
            throw new JedisException("put value into redis error! key:" + key + " value: " + value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return "OK".equals(ans);
    }


    /**
     * 不存在时，写入；否则返回写入失败
     * @param key
     * @param value
     * @return
     */
    public boolean setnx(String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("key || value || expire are illegal");
        }

        Jedis jedis = null;
        JedisPool pool = getJedisPool(MASTER_JEDIS);
        Long ans;
        try {
            jedis = pool.getResource();
            ans = jedis.setnx(key, value);
        } catch (Exception e) {
            logger.error("set string into cache error! key:{}, value:{},  e:{}", key, value, e);
            throw new JedisException("put value into redis error! key:" + key + " value: " + value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        // 返回1， 表示写入成功； 0 表示已经存在了
        return Objects.equals(1, ans);
    }


    /**
     * 入队， 利用 rpush
     * @param key
     * @param value
     * @return
     */
    public boolean push(String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("key || value || expire are illegal");
        }

        Jedis jedis = null;
        JedisPool pool = getJedisPool(MASTER_JEDIS);
        try {
            jedis = pool.getResource();
            long ans = jedis.rpush(key, value);

            return ans > 0;
        } catch (Exception e) {
            logger.error("push string into cache error! key:{}, value:{},  e:{}", key, value, e);
            throw new JedisException("put value into redis error! key:" + key + " value: " + value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public String pop(String key) {
        if (StringUtils.isBlank(key) ) {
            throw new IllegalArgumentException("key should not be empty!");
        }

        Jedis jedis = null;
        JedisPool pool = getJedisPool(MASTER_JEDIS);
        String ans;
        try {
            jedis = pool.getResource();
            ans = jedis.lpop(key);
            return ans;
        } catch (Exception e) {
            logger.error("pop string from cache error! key:{},   e:{}", key, e);
            throw new JedisException("pop value from redis error! key:" + key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
