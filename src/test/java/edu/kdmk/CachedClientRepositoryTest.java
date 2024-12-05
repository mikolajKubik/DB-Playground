package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.cache.CachedClientRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CachedClientRepositoryTest {

    private static MongoConfig mongoConfig;
    private static RedisConfig redisConfig;
    Client client;


    @BeforeAll
    static void setup() {
        mongoConfig = new MongoConfig();
        redisConfig = new RedisConfig();
    }

    @AfterAll
    static void tearDown() {
        try {
            if (mongoConfig != null) {
                mongoConfig.close();
            }
        } catch (Exception e) {
            return;
        }
    }

    @BeforeEach
    void setupClient() {
        UUID id = UUID.randomUUID();
        client = new Client(id, "Ziut", "Deer", "123 Main St", 0);
    }

    private boolean isKeyInRedis(String key) {
        return redisConfig.getRedisJsonClient().exists("client:" + key);
    }

    @Test
    void insertClientTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(isKeyInRedis(client.getId().toString()));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertInstanceOf(Client.class, clientManager.findClientById(client.getId()).get());
    }

    @Test
    void deleteClientPositiveTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(isKeyInRedis(client.getId().toString()));

        assertTrue(clientManager.deleteClientById(client.getId()));

        assertFalse(isKeyInRedis(client.getId().toString()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingClientTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());

        assertFalse(isKeyInRedis(client.getId().toString()));

        assertTrue(clientManager.deleteClientById(client.getId()));
    }

    @Test
    void updateClientTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(isKeyInRedis(client.getId().toString()));

        client.setFirstName("Ziut2");

        assertTrue(clientManager.updateClient(client));

        assertTrue(isKeyInRedis(client.getId().toString()));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertEquals(client.getFirstName(), clientManager.findClientById(client.getId()).get().getFirstName());
    }

    @Test
    void invalidateClientInRedisTest() {
        CachedClientRepository cachedClientRepository = new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient());
        cachedClientRepository.insert(client);

        assertTrue(isKeyInRedis(client.getId().toString()));

        cachedClientRepository.invalidateCache(client.getId());

        assertFalse(isKeyInRedis(client.getId().toString()));
    }

    @Test
    void invalidateCache() {
        CachedClientRepository cachedClientRepository = new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient());
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        cachedClientRepository.insert(new Client(id1, "John1", "Deer", "123 Main St", 0));
        cachedClientRepository.insert(new Client(id2, "John2", "Deer", "123 Main St", 0));

        assertTrue(isKeyInRedis(id1.toString()));
        assertTrue(isKeyInRedis(id2.toString()));

        redisConfig.clearCache();

        assertFalse(isKeyInRedis(id1.toString()));
        assertFalse(isKeyInRedis(id2.toString()));
    }
}
