package com.ces.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Tuple;

/**
 * 管理到各redis集群的连接，封装通用的redis原子操作。 redis在部署时从物理上隔离为五个集群，分别存储用户在线状态、漫游消息、http
 * sessionKey、杂七杂八的东西、需要持久化的记录
 * 
 * @author liyalong
 */
public abstract class AbstractRedisDevice {

    private static Logger logger = Logger.getLogger(AbstractRedisDevice.class);

    private Map<RedisNodeType, RedisConnection> connMap = new HashMap<RedisNodeType, RedisConnection>();

    public AbstractRedisDevice() {
        for (RedisNodeType type: RedisNodeType.values()) {
            RedisConnection conn = new RedisConnection(type.getProxyFull());
            connMap.put(type, conn);
        }
    }

    protected RedisConnection getConnection(RedisNodeType nodeType) {
        return connMap.get(nodeType);
    }

    public static String combineKey(Object... keys) {
        StringBuilder sb = new StringBuilder();
        for (Object key: keys) {
            sb.append(key).append("|");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static String combineKey(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key: keys) {
            sb.append(key).append("|");
        }
        return sb.substring(0, sb.length() - 1);
    }

    protected String generateKey(String tag, Object key) {
        return (key == null) ? tag : (tag + "|" + key);
    }

    protected String generateKey(String tag, Object... keys) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        for (Object key: keys) {
            sb.append("|").append(key);
        }
        return sb.toString();
    }

    // --------------------------------------
    // key
    // --------------------------------------

    public long del(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.del(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return 0;
    }

    public boolean expire(RedisNodeType nodeType, String tag, Object key,
        int seconds) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            jedis.expire(generateKey(tag, key), seconds);
            return true;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public boolean expireAt(RedisNodeType nodeType, String tag, Object key,
        long unixTime) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            jedis.expireAt(generateKey(tag, key), unixTime);
            return true;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public Long ttl(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.ttl(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    public boolean isExist(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.exists(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public String type(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.type(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    // --------------------------------------
    // string/bytes
    // --------------------------------------

    public Long incr(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.incr(generateKey(tag, key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            rc.onException(jedis);
            // jedis.incr(key);
        } finally {
            rc.onFinally(jedis);
        }
        return 0L;
    }

    public Long incrBy(RedisNodeType nodeType, String tag, Object key,
        long value) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.incrBy(generateKey(tag, key), value);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }
    
    public Long decr(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.decr(generateKey(tag, key));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            rc.onException(jedis);
            // jedis.incr(key);
        } finally {
            rc.onFinally(jedis);
        }
        return 0L;
    }

    public boolean setex(RedisNodeType nodeType, String tag, Object key,
        String value, int expireSeconds) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            String ret = jedis.setex(generateKey(tag, key), expireSeconds,
                value);
            return "OK".equals(ret);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public boolean set(RedisNodeType nodeType, String tag, Object key,
        String value) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            String ret = jedis.set(generateKey(tag, key), value);
            return "OK".equals(ret);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public boolean set(RedisNodeType nodeType, String tag, Object key,
        Object value) {
        return set(nodeType, tag, key, value.toString());
    }

    public boolean setnx(RedisNodeType nodeType, String tag, Object key,
        String value) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            Long ret = jedis.setnx(generateKey(tag, key), value);
            return ret == 1;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public String get(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.get(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
            // return jedis.get(key);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    public byte[] getBytes(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.get(generateKey(tag, key).getBytes());
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    public Long getLong(RedisNodeType nodeType, String tag, Object key) {
        String value = get(nodeType, tag, key);
        return (value == null) ? null : Long.parseLong(value);
    }

    public Integer getInt(RedisNodeType nodeType, String tag, Object key) {
        String value = get(nodeType, tag, key);
        return (value == null) ? null : Integer.parseInt(value);
    }

    // --------------------------------------
    // set
    // --------------------------------------

    public long sadd(RedisNodeType nodeType, String tag, Object key,
        String... values) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.sadd(generateKey(tag, key), values);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return 0;
    }

    public long srem(RedisNodeType nodeType, String tag, Object key,
        String... values) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.srem(generateKey(tag, key), values);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return 0;
    }

    public boolean sismember(RedisNodeType nodeType, String tag, Object key,
        String value) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.sismember(generateKey(tag, key), value);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    /**
     * @return true：value全部有效，false：至少有一个value无效
     */
    public boolean sismember(RedisNodeType nodeType, String tag, Object key,
        String... values) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            Pipeline pl = jedis.pipelined();
            List<Response<Boolean>> retList = new ArrayList<Response<Boolean>>();
            String combinedKey = generateKey(tag, key);
            for (String value: values) {
                Response<Boolean> ret = pl.sismember(combinedKey, value);
                retList.add(ret);
            }
            pl.sync();

            for (Response<Boolean> ret: retList) {
                if (!ret.get())
                    return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return false;
    }

    public long scard(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.scard(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return 0;
    }

    public Set<String> smembers(RedisNodeType nodeType, String tag, Object key) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.smembers(generateKey(tag, key));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    public Map<String, Long> scard(RedisNodeType nodeType, String tag,
        Collection<?> keys) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            Map<String, Response<Long>> retMap = new HashMap<String, Response<Long>>();
            Pipeline pl = jedis.pipelined();
            for (Object key: keys) {
                String theKey = generateKey(tag, key);
                Response<Long> ret = pl.scard(theKey);
                retMap.put(theKey, ret);
            }
            pl.sync();

            Map<String, Long> retMap2 = new HashMap<String, Long>();
            for (Map.Entry<String, Response<Long>> entry: retMap.entrySet()) {
                retMap2.put(entry.getKey(), entry.getValue().get());
            }
            return retMap2;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    public Map<String, Set<String>> smembers(RedisNodeType nodeType,
        String tag, Collection<?> keys) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            Map<String, Response<Set<String>>> retMap = new HashMap<String, Response<Set<String>>>();
            Pipeline pl = jedis.pipelined();
            for (Object key: keys) {
                String theKey = generateKey(tag, key);
                Response<Set<String>> ret = pl.smembers(theKey);
                retMap.put(theKey, ret);
            }
            pl.sync();

            Map<String, Set<String>> retMap2 = new HashMap<String, Set<String>>();
            for (Map.Entry<String, Response<Set<String>>> entry: retMap
                .entrySet()) {
                retMap2.put(entry.getKey(), entry.getValue().get());
            }
            return retMap2;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    // --------------------------------------
    // sorted set
    // --------------------------------------

    public Map<String, Double> zrangeWithScores(RedisNodeType nodeType,
        String tag, Object key, long start, long end) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            Set<Tuple> ret = jedis.zrangeWithScores(generateKey(tag, key),
                start, end);

            Map<String, Double> retMap = new LinkedHashMap<String, Double>();
            for (Tuple tuple: ret) {
                retMap.put(tuple.getElement(), tuple.getScore());
            }
            return retMap;
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    public Double zscore(RedisNodeType nodeType, String tag, Object key,
        String member) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.zscore(generateKey(tag, key), member);
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

    // --------------------------------------
    // script
    // --------------------------------------

    public Object evalsha(RedisNodeType nodeType, String script,
        List<String> keys, String... args) {
        RedisConnection rc = getConnection(nodeType);
        Jedis jedis = rc.getConnection();
        try {
            return jedis.evalsha(script, keys, Arrays.asList(args));
        } catch (Exception e) {
            logger.error("redis error", e);
            rc.onException(jedis);
        } finally {
            rc.onFinally(jedis);
        }
        return null;
    }

}
