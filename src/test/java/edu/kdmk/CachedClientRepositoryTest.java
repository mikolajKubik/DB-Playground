package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.config.RedisConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
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
        client = new Client(UUID.randomUUID(), "Ziut", "Deer", "123 Main St", 0);
    }

    @Test
    void insertClientTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertInstanceOf(Client.class, clientManager.findClientById(client.getId()).get());
    }

    @Test
    void deleteClientPositiveTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(clientManager.deleteClientById(client.getId()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingClientTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());

        assertTrue(clientManager.deleteClientById(client.getId()));
    }

    @Test
    void updateClientTest() {
        ClientManager clientManager = new ClientManager(new CachedClientRepository(mongoConfig.getDatabase(), redisConfig.getRedisJsonClient()));

        assertTrue(clientManager.insertClient(client));

        client.setFirstName("Ziut2");

        assertTrue(clientManager.updateClient(client));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertEquals(client.getFirstName(), clientManager.findClientById(client.getId()).get().getFirstName());
    }
}
