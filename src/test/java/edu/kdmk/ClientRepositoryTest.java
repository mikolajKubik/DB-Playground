package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.models.Client;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {
    private static MongoConfig mongoConfig;
    Client client;

    @BeforeAll
    static void setup() {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";

        // Initialize MongoConfig before all tests
        mongoConfig = new MongoConfig(connectionString, databaseName);

        System.out.println("MongoDB connection setup before all Client tests");
    }

    @AfterAll
    static void tearDown() {
        try {
            // Close MongoConfig after all tests
            if (mongoConfig != null) {
                mongoConfig.close();
            }
            System.out.println("MongoDB connection closed after all Client tests");
        } catch (Exception e) {
            System.out.println("Error closing MongoDB connection");
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupClient() {
        client = new Client("Ziut", "Deer", "123 Main St");
    }

    @Test
    void insertClientTest() {
        ClientManager clientManager = new ClientManager(mongoConfig.getDatabase());

        assertTrue(clientManager.insertClient(client));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertEquals(client, clientManager.findClientById(client.getId()).get());
    }

    @Test
    void deleteClientPositiveTest() {
        ClientManager clientManager = new ClientManager(mongoConfig.getDatabase());

        assertTrue(clientManager.insertClient(client));

        assertTrue(clientManager.deleteClientById(client.getId()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingClientTest() {
        ClientManager clientManager = new ClientManager(mongoConfig.getDatabase());

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());

        assertTrue(clientManager.deleteClientById(client.getId()));
    }

    @Test
    void updateClientTest() {
        ClientManager clientManager = new ClientManager(mongoConfig.getDatabase());

        assertTrue(clientManager.insertClient(client));

        client.setFirstName("Ziut2");

        assertTrue(clientManager.updateClient(client));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertEquals(client.getFirstName(), clientManager.findClientById(client.getId()).get().getFirstName());
    }
}
