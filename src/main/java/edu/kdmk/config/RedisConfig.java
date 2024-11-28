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
import lombok.Getter;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.json.commands.RedisJsonCommands;

public class RedisConfig {
    private final JedisPooled jedisPooled;

//    public RedisConfig() {
//        JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().build();
//        this.jedisPooled = new JedisPooled(new HostAndPort("localhost", 6379), jedisClientConfig);
//    }
public RedisConfig() {
    // Load environment variables from .env
    Dotenv dotenv = Dotenv.load();

    // Get host and port from environment variables
    String redisHost = dotenv.get("REDIS_HOST"); // Default to "localhost" if not set
    int redisPort = Integer.parseInt(dotenv.get("REDIS_PORT")); // Default to 6379 if not set

    // Configure Jedis
    JedisClientConfig jedisClientConfig = DefaultJedisClientConfig.builder().build();
    this.jedisPooled = new JedisPooled(new HostAndPort(redisHost, redisPort), jedisClientConfig);
}

    public JedisPooled getRedisJsonClient() {
        return jedisPooled; // JedisPooled implements RedisJsonCommands
    }
}
