package edu.kdmk.config;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

public class RedisConfig implements AutoCloseable {
    private final JedisPooled jedisPooled;

    public RedisConfig() {
        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().build();

        this.jedisPooled = new JedisPooled(new HostAndPort("localhost", 6379), jedisClientConfig);
    }

    public JedisPooled getJedisPooled() {
        return jedisPooled;
    }

    @Override
    public void close() throws Exception {
        if (jedisPooled != null) {
            jedisPooled.close();
        }
    }
}
