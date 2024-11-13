package edu.kdmk;

import com.mongodb.client.MongoDatabase;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClientRepositoryTest {
    private static MongoConfig mongoConfig;

    @BeforeAll
    static void setup() {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";

        // Initialize MongoConfig before all tests
        mongoConfig = new MongoConfig(connectionString, databaseName);

        System.out.println("MongoDB connection setup before all tests");
    }

    @AfterAll
    static void tearDown() {
        try {
            // Close MongoConfig after all tests
            if (mongoConfig != null) {
                mongoConfig.close();
            }
            System.out.println("MongoDB connection closed after all tests");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void insertClientTest() {
        ClientManager gameManager = new ClientManager(mongoConfig.getDatabase());
    }
}
