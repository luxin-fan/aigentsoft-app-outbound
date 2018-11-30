package com.ces.oubound.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.outbound.conf.InitConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisConnection {
    private static Logger logger = Logger.getLogger(RedisConnection.class);

    private String type;

    private List<String> redisHostList;

    private String currentHost;

    private static final JedisPoolConfig config = new JedisPoolConfig();
    static {
        config.setMaxIdle(100);
        config.setMaxActive(100);
        config.setMaxWait(3000);
        // jedis检测连接时会发ping，但nutcracker不支持，会导致该连接立即被关掉，因此把检测禁用掉
        config.setTestWhileIdle(false);
    }

    private JedisPool jedisPool;

    public RedisConnection(String type) {
        this.type = type;
        parseHostList();

        initPool();
    }

    private void parseHostList() {
        String list = InitConfig.DB_URL;
        if (list == null) {
            logger.warn("no redis host, type: " + type);
            return;
        }

        redisHostList = new ArrayList<String>(Arrays.asList(list.split("\\s")));
    }

    private void initPool() {
        if (redisHostList == null)
            return;

        if (redisHostList.isEmpty()) {
            logger.warn("reload redis host, type: " + type);
            parseHostList();
        }

        logger.info("init pool, type: " + type + ", addr: " + redisHostList);

        Random rand = new Random();
        currentHost = redisHostList.get(rand.nextInt(redisHostList.size()));

        String[] tmp = currentHost.split(":");
        String ip = tmp[0];
        int port = Integer.valueOf(tmp[1]);

        try {
            int timeout = 3000;
            if(InitConfig.DB_PWD == null || InitConfig.DB_PWD.length() == 0){
            	jedisPool = new JedisPool(config, ip, port, timeout);
            }else{
            	jedisPool = new JedisPool(config, ip, port, timeout, InitConfig.DB_PWD);
            }
            logger.info("connection : [" + getConnection() +"]");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Jedis getConnection() {
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            logger.error(e.getMessage(), e);
            redisHostList.remove(currentHost);
            currentHost = null;
          //  initPool();
        }
        return null;
    }

    public void onFinally(Jedis jedis) {
        if (jedis != null) {
            // jedis is multi ,need discard all opes
            if (jedis.getClient().isInMulti()) {
                jedis.getClient().discard();
                jedis.disconnect();
            }

            if (jedisPool != null) {
                jedisPool.returnResource(jedis);
            }
        }
    }

    public void onException(Jedis jedis) {
        if (jedis != null) {
            // jedis is multi ,need discard all opes
            if (jedis.getClient().isInMulti()) {
                jedis.getClient().discard();
                jedis.disconnect();
            }

            if (jedisPool != null) {
                jedisPool.returnBrokenResource(jedis);
            }
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
    }
}
