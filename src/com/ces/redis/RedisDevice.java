package com.ces.redis;

import java.util.Set;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.ces.redis.buffer.IOriginalBuffer;
import com.ces.redis.buffer.OriginalBufferImpl;

/**
 * 本类主要存放对redis的复杂操作，基本的原子操作都在父类
 */
public class RedisDevice extends AbstractRedisDevice {

	public static final Logger logger = Logger.getLogger(RedisDevice.class);
	/*
	 */
	public static final String ROSTER_TAG = "roster"; // 会话列表：tag+uid,

	private final static String hashTagS = "{";

	private final static String hashTagE = "}";

	/**
	 * redis 本身对外服务类
	 */
	private IOriginalBuffer originalBuffer = null;

	public RedisDevice() {
		setOriginalBuffer(new OriginalBufferImpl(
				getConnection(RedisNodeType.ROSTER)));
	}

	/**
	 * 获取redis Original操作服务类
	 * 
	 * @return
	 */
	public IOriginalBuffer getOriginalBuffer() {
		return this.originalBuffer;
	}
	
	public String getCallRoster(String rosterId) {
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		byte[] value;
		try {
			value = jedis.get(generateKey(ROSTER_TAG,rosterId).getBytes());
			if (value == null || value.length == 0)
				return null;
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
			return null;
		} finally {
			rc.onFinally(jedis);
		}
		return new String(value);
	}
	
	public int getkeysNum(String rosterId) {
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		Set<byte[]> value;
		try {
			value = jedis.keys(generateKey(ROSTER_TAG,rosterId).getBytes());
			if (value == null )
				return 0;
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
			return 0;
		} finally {
			rc.onFinally(jedis);
		}
		return value.size();
	}
	
	// 坐席状态
	// --------------------------------------
	public boolean setCallRoster(String rosterId, String rosterInfo) {
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		try {
			// [!] 存取时的参数类型必须一致，要么都是string，要么都是bytes
			jedis.set(generateKey(ROSTER_TAG, rosterId).getBytes(),rosterInfo.getBytes());
			return true;
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
		} finally {
			rc.onFinally(jedis);
		}
		return false;
	}
	
	public boolean expireCallRoster(String rosterId) {
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		try {
			// [!] 存取时的参数类型必须一致，要么都是string，要么都是bytes
			jedis.expire(generateKey(ROSTER_TAG, rosterId).getBytes(),120);
			return true;
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
		} finally {
			rc.onFinally(jedis);
		}
		return false;
	}

	
	public Long delCallRoster(String rosterId) {
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		Long value = null;
		try {
			value = jedis.del(generateKey(ROSTER_TAG, rosterId)
					.getBytes());
			return value;
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
			return value;
		} finally {
			rc.onFinally(jedis);
		}
	}
	// --------------------------------------
	// 持久化
	// --------------------------------------

	public Long incr(String tag, Object key) {
		return incr(RedisNodeType.ROSTER, tag, key);
	}

	public Long incrBy(String tag, Object key, long value) {
		return incrBy(RedisNodeType.ROSTER, tag, key, value);
	}

	public boolean set(String tag, Object key, String value, int seconds) {
		return setex(RedisNodeType.ROSTER, tag, key, value, seconds);
	}

	public boolean set(String tag, Object key, String value) {
		return set(RedisNodeType.ROSTER, tag, key, value);
	}

	public boolean setnx(String tag, Object key, String value) {
		return setnx(RedisNodeType.ROSTER, tag, key, value);
	}

	public boolean set(String tag, Object key, Object value) {
		return set(tag, key, value.toString());
	}

	public String get(String tag, Object key) {
		return get(RedisNodeType.ROSTER, tag, key);
	}

	public Long getLong(String tag, Object key) {
		String value = get(tag, key);
		return (value == null) ? null : Long.parseLong(value);
	}

	public Integer getInt(String tag, Object key) {
		String value = get(tag, key);
		return (value == null) ? null : Integer.parseInt(value);
	}

	public boolean del(String tag, Object key) {
		long ret = del(RedisNodeType.ROSTER, tag, key);
		return ret > 0;
	}

	public boolean expire(String tag, Object key, int seconds) {
		return expire(RedisNodeType.ROSTER, tag, key, seconds);
	}

	public boolean expireAt(String tag, Object key, long unixTime) {
		return expireAt(RedisNodeType.ROSTER, tag, key, unixTime);
	}

	public Long ttl(String tag, Object key) {
		return ttl(RedisNodeType.ROSTER, tag, key);
	}

	public boolean isExist(String tag, Object key) {
		return isExist(RedisNodeType.ROSTER, tag, key);
	}

	public static class LimitRate {
		// 检查间隔 单位秒
		public int checkPeriod;

		// 间隔内运行的次数
		public int checkTimes;

		// 超限后的屏蔽时间,单位秒
		public int banPeriod;

		/**
		 * checkPeriod秒内允许checktimes次请求, 超过就屏蔽banPeriod秒
		 * 
		 * @param checkPeriod
		 * @param checkTimes
		 * @param banPeriod
		 */

		public LimitRate(int checkPeriod, int checkTimes, int banPeriod) {
			this.checkPeriod = checkPeriod;
			this.checkTimes = checkTimes;
			this.banPeriod = banPeriod;
		}

		public LimitRate() {
		}
	}

	/*
	 * public void delMapNotify(long key, long start, long end) { Jedis jedis =
	 * rc.getConnection(); try { jedis.zremrangeByScore(
	 * generateKey(RedisDevice.MAP_NOTIFY_TAG, key), start, end); } catch
	 * (Exception e) { logger.error("redis error", e); rc.onException(jedis); }
	 * finally { rc.onFinally(jedis); } }
	 */

	public void setAskCount(String tag, long uid, int count, int seconds) {
		String key = generateKey(tag, uid);
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		try {
			jedis.set(key, count + "");
			jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
		} finally {
			rc.onFinally(jedis);
		}
	}

	public Integer getAskCount(String tag, long uid) {
		String key = generateKey(tag, uid);
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		try {
			if (!jedis.exists(key))
				return null;
			return Integer.parseInt(jedis.get(key));
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
		} finally {
			rc.onFinally(jedis);
		}
		return null;
	}

	public boolean incrByAskCount(String tag, long uid, int count, int seconds) {
		String key = generateKey(tag, uid);
		RedisConnection rc = getConnection(RedisNodeType.ROSTER);
		Jedis jedis = rc.getConnection();
		try {
			if (!jedis.exists(key))
				return false;
			jedis.incrBy(key, count);
			jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error("redis error", e);
			rc.onException(jedis);
		} finally {
			rc.onFinally(jedis);
		}
		return true;
	}

	/**
	 * 把redis的key增加nutcracker需要的hashtag,以保证同样的hashtag范围内的key,落在同一个redis节点上,
	 * 以便可以在同一个节点使用set的与或操作 Example formatHashtag("abcdefg",1,3) = "a{bc}defg"
	 * 
	 * @param src
	 * @param start
	 * @return
	 */
	private String formatHashtag(String src, int start, int end) {
		if (src == null || src.length() < end || start > end || start < 0
				|| end < 0)
			throw new IllegalArgumentException();
		StringBuilder sb = new StringBuilder();
		if (start == 0) {
			sb.append(hashTagS);

		} else {
			sb.append(src.substring(0, start));
			sb.append(hashTagS);
		}

		sb.append(src.substring(start, end));
		sb.append(hashTagE);
		sb.append(src.substring(end, src.length()));
		return sb.toString();
	}
	
	public void setOriginalBuffer(IOriginalBuffer originalBuffer) {
		this.originalBuffer = originalBuffer;
	}
}
