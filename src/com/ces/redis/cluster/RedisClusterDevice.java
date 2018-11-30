package com.ces.redis.cluster;

import java.util.Map;
import java.util.Set;

/**
 * redis分布式操作的接口，其实例由 {@link RedisClusterProxy} 创建
 * 
 * @author liyalong
 */
public interface RedisClusterDevice {

    public Map<String, String> getInfo();

    public Map<String, Set<String>> getKeys(String pattern);

    public Map<String, String> scriptLoad(String script);

    public Map<String, Boolean> scriptExists(String sha1);

    public Map<String, String> scriptFlush();

    public Map<String, String> getSlowLog();
}
