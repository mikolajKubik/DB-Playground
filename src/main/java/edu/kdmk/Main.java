package edu.kdmk;

import edu.kdmk.config.RedisConfig;

public class Main {
    public static void main(String[] args) {
        try (RedisConfig redisConfig = new RedisConfig()) {
            // Set a value in Redis
            redisConfig.getJedisPooled().set("test-key", "test-value");
            System.out.println("Value set in Redis.");

            // Get the value from Redis
            String value = redisConfig.getJedisPooled().get("test-key");
            System.out.println("Value retrieved from Redis: " + value);

            // Delete the key
            redisConfig.getJedisPooled().del("test-key");
            System.out.println("Key deleted from Redis.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}