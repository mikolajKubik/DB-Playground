//package edu.kdmk.config;
//
//import redis.clients.jedis.DefaultJedisClientConfig;
//import redis.clients.jedis.HostAndPort;
//import redis.clients.jedis.JedisClientConfig;
//import redis.clients.jedis.UnifiedJedis;
//
//public class RedisConfig implements AutoCloseable {
//    private final JedisClientConfig jedisClientConfig;
//    private final UnifiedJedis unifiedJedis;
//
//    public RedisConfig() {
//        this.jedisClientConfig = DefaultJedisClientConfig.builder().build();
//        this.unifiedJedis = new UnifiedJedis(new HostAndPort("localhost", 6379), jedisClientConfig);
//    }
//
//    public UnifiedJedis getRedisJsonClient() {
//        return unifiedJedis;
//    }
//
//    @Override
//    public void close() {
//        if (unifiedJedis != null) {
//            unifiedJedis.close();
//        }
//    }
//}

package edu.kdmk.config;

import lombok.Getter;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.commands.RedisJsonCommands;

public class RedisConfig {
    private final JedisPooled jedisPooled;

    public RedisConfig() {
        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().build();
        this.jedisPooled = new JedisPooled(new HostAndPort("localhost", 6379), jedisClientConfig);
    }

    public JedisPooled getRedisJsonClient() {
        return jedisPooled; // JedisPooled implements RedisJsonCommands
    }
}
