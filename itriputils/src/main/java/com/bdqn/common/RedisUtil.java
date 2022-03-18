package com.bdqn.common;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class RedisUtil {

    private Jedis jedis;

    public void setRedis(String key, String value) {
        jedis = new Jedis("localhost", 6379);
        jedis.auth("123456");
        jedis.setex(key, 7200, value);
    }

    public String getStr(String key) {
        jedis = new Jedis("localhost", 6379);
        jedis.auth("123456");
        return jedis.get(key);
    }

}
