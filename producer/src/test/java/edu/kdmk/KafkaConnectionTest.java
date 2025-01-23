package edu.kdmk;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaConnectionTest {

    private static AdminClient adminClient;

    @BeforeAll
    public static void setup() {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        adminClient = AdminClient.create(config);
    }

    @Test
    public void testConnection() {
        try {
            assertTrue(adminClient.listTopics().names().get().size() >= 0);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            assertTrue(false, "Failed to connect to Kafka cluster");
        }
    }

    @AfterAll
    public static void teardown() {
        if (adminClient != null) {
            adminClient.close();
        }
    }
}
