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

import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

public class RedisConfig {
    private final JedisPooled jedisPooled;

    public RedisConfig() {
        // Load environment variables from .env
        Dotenv dotenv = Dotenv.load();

        String redisHost = dotenv.get("REDIS_HOST"); // Default to "localhost" if not set
        int redisPort = Integer.parseInt(dotenv.get("REDIS_PORT")); // Default to 6379 if not set

        // Configure Jedis
        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().build();
        this.jedisPooled = new JedisPooled(new HostAndPort(redisHost, redisPort), jedisClientConfig);
    }

    public JedisPooled getRedisJsonClient() {
        return jedisPooled;
    }

    public void clearCache() {
        if (jedisPooled != null) {
            jedisPooled.flushAll();
        }
    }
}
