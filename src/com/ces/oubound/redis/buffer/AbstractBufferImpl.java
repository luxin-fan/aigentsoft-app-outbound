/**
 * @(#)AbstractBufferImpl.java, 2013-6-17.
 *
 * Copyright 2013 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ces.oubound.redis.buffer;

import com.ces.oubound.redis.RedisConnection;

/**
 * redis缓存接口服务实现父类
 * 
 * @author jiangzhouyun@corp.netease.com
 */
public class AbstractBufferImpl {

    protected RedisConnection conn;

    public AbstractBufferImpl(RedisConnection conn) {
        if (conn == null) {
            throw new NullPointerException("RedisConnection is not null!");
        }

        this.conn = conn;
    }

    /**
     * @return the conn
     */
    public RedisConnection getConn() {
        return conn;
    }

    /**
     * @param conn
     *            the conn to set
     */
    public void setConn(RedisConnection conn) {
        this.conn = conn;
    }

}
