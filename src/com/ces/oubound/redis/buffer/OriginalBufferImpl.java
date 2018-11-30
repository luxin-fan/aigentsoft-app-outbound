package com.ces.oubound.redis.buffer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ces.oubound.redis.RedisConnection;

import redis.clients.jedis.Jedis;

/**
 * Redis 原型接口的基本实现
 * 
 * @author jiangzhouyun@corp.netease.com
 */
public class OriginalBufferImpl extends AbstractBufferImpl implements
		IOriginalBuffer {

	private static final Logger logger = Logger
			.getLogger(OriginalBufferImpl.class);

	/**
	 * @param conn
	 */
	public OriginalBufferImpl(RedisConnection conn) {
		super(conn);
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.set(key, value);
			return ret;
		} catch (Exception e) {
			logger.error("redis set error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public String get(String key) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.get(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis get error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.hset(key, field, value);

			return ret;
		} catch (Exception e) {
			logger.error("redis hset error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public String hget(String key, String field) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.hget(key, field);

			return ret;
		} catch (Exception e) {
			logger.error("redis hget error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.hsetnx(key, field, value);

			return ret;
		} catch (Exception e) {
			logger.error("redis hsetnx error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.hmset(key, hash);

			return ret;
		} catch (Exception e) {
			logger.error("redis hmset error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public List<String> hmget(String key, String... fields) {
		Jedis jedis = conn.getConnection();
		try {
			List<String> ret = jedis.hmget(key, fields);

			return ret;
		} catch (Exception e) {
			logger.error("redis hmget error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long hincrBy(String key, String field, long value) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.hincrBy(key, field, value);

			return ret;
		} catch (Exception e) {
			logger.error("redis hincrBy error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Boolean hexists(String key, String field) {
		Jedis jedis = conn.getConnection();
		try {
			Boolean ret = jedis.hexists(key, field);

			return ret;
		} catch (Exception e) {
			logger.error("redis hexists error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long hdel(String key, String field) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.hdel(key, field);

			return ret;
		} catch (Exception e) {
			logger.error("redis hdel error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long hlen(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.hlen(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis hlen error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> hkeys(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.hkeys(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis hkeys error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public List<String> hvals(String key) {
		Jedis jedis = conn.getConnection();
		try {
			List<String> ret = jedis.hvals(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis hvals error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Map<String, String> ret = jedis.hgetAll(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis hgetAll error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Boolean exists(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Boolean ret = jedis.exists(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis exists error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long del(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.del(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis del error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long expire(String key, int seconds) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.expire(key, seconds);

			return ret;
		} catch (Exception e) {
			logger.error("redis expire error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long expireAt(String key, long unixTime) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.expireAt(key, unixTime);

			return ret;
		} catch (Exception e) {
			logger.error("redis expireAt error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long lpush(String key, String value) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.lpush(key, value);

			return ret;
		} catch (Exception e) {
			logger.error("redis lpush error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long llen(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.llen(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis llen error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = conn.getConnection();
		try {
			List<String> ret = jedis.lrange(key, start, end);

			return ret;
		} catch (Exception e) {
			logger.error("redis lrange error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public String ltrim(String key, long start, long end) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.ltrim(key, start, end);

			return ret;
		} catch (Exception e) {
			logger.error("redis ltrim error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public String lindex(String key, long index) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.lindex(key, index);

			return ret;
		} catch (Exception e) {
			logger.error("redis lindex error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long lrem(String key, long count, String value) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.lrem(key, count, value);

			return ret;
		} catch (Exception e) {
			logger.error("redis lrem error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long sadd(String key, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.sadd(key, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis sadd error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> smembers(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.smembers(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis smembers error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long srem(String key, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.srem(key, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis srem error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public String spop(String key) {
		Jedis jedis = conn.getConnection();
		try {
			String ret = jedis.spop(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis spop error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.smove(srckey, dstkey, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis smove error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long scard(String key) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.scard(key);

			return ret;
		} catch (Exception e) {
			logger.error("redis scard error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Boolean sismember(String key, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Boolean ret = jedis.sismember(key, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis sismember error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long zadd(String key, double score, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.zadd(key, score, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis zadd error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> zrange(String key, int start, int end) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.zrange(key, start, end);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrange error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long zrem(String key, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.zrem(key, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrem error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Double zincrby(String key, double score, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Double ret = jedis.zincrby(key, score, member);

			return ret;
		} catch (Exception e) {
			logger.error("redis zincrby error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Long zcount(String key, double min, double max) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.zcount(key, min, max);

			return ret;
		} catch (Exception e) {
			logger.error("redis zcount error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.zrangeByScore(key, min, max);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrangeByScore error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.zrangeByScore(key, min, max);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrangeByScore error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max,
			int offset, int count) {
		Jedis jedis = conn.getConnection();
		try {
			// Set<String> ret = jedis.zrangeByScore(key, min, max);
			Set<String> ret = jedis.zrangeByScore(key, min, max, offset, count);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrangeByScore error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.zrevrangeByScore(key, max, min);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrevrangeByScore error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min,
			int offset, int count) {
		Jedis jedis = conn.getConnection();
		try {
			Set<String> ret = jedis.zrevrangeByScore(key, max, min, offset,
					count);

			return ret;
		} catch (Exception e) {
			logger.error("redis zrevrangeByScore error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public Double zscore(String key, String member) {
		Jedis jedis = conn.getConnection();
		try {
			Double ret = jedis.zscore(key, member);
			return ret;
		} catch (Exception e) {
			logger.error("redis zscore error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}

	@Override
	public void subscribe(final String channel) {
	}

	@Override
	public Long publish(final String channel, final String message) {
		Jedis jedis = conn.getConnection();
		try {
			Long ret = jedis.publish(channel, message);
			return ret;
		} catch (Exception e) {
			logger.error("redis publish error", e);
			conn.onException(jedis);
			return null;
		} finally {
			conn.onFinally(jedis);
		}
	}
}
