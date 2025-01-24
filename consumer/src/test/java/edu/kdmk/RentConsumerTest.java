package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.repository.RentRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RentConsumerTest {
    private static MongoConfig mongoConfig;
    private static RentRepository rentRepository;

    @BeforeAll
    static void setUp() {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";
        mongoConfig = new MongoConfig(connectionString, databaseName);
        rentRepository = new RentRepository(mongoConfig.getDatabase());
    }

    @Test
    void test() throws InterruptedException {

        RentConsumer rentConsumer = new RentConsumer(rentRepository);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<KafkaConsumer<UUID, String>> consumers = rentConsumer.getConsumersGroup();

        for (KafkaConsumer<UUID, String> consumer : consumers) {
            executorService.execute(() -> {
                rentConsumer.consume(consumer);
            });
        }
        Thread.sleep(100000);
        for (KafkaConsumer<UUID, String> consumer : consumers) {
            consumer.close();
        }
        executorService.shutdown();
    }
}
