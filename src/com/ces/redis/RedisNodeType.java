package com.ces.redis;

public enum RedisNodeType {

    ROSTER("RedisProxyForRoster", "RedisRawNodeForRoster", "roster"),

    METRIC("RedisProxyForMetric", "RedisRawNodeForMetric", "metric");

    private String proxyFull;

    private String rawFull;

    private String abbr;

    private RedisNodeType(String proxyFull, String rawFull, String abbr) {
        setProxyFull(proxyFull);
        setRawFull(rawFull);
        setAbbr(abbr);
    }

    public String getProxyFull() {
        return proxyFull;
    }

    public void setProxyFull(String proxyFull) {
        this.proxyFull = proxyFull;
    }

    public String getRawFull() {
        return rawFull;
    }

    public void setRawFull(String rawFull) {
        this.rawFull = rawFull;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public static RedisNodeType getTypeByAbbr(String abbr) {
        if (abbr.equals(ROSTER.abbr))
            return ROSTER;
        else if (abbr.equals(METRIC.abbr))
            return METRIC;
        else
            return null;
    }
}
