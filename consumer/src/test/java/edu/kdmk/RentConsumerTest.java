package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.repository.RentRepository;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
    void test1() throws InterruptedException {

        RentConsumer rentConsumer = new RentConsumer(rentRepository);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<KafkaConsumer<UUID, String>> consumers = rentConsumer.getConsumersGroup();

        // Przypisujemy każdego konsumenta do osobnego wątku
        consumers.forEach(consumer ->
                executorService.execute(() -> {
                    try {
                        rentConsumer.consume(consumer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            consumer.close();
                        } catch (Exception closeException) {
                            closeException.printStackTrace();
                        }
                    }
                })
        );

        // Czekamy na zakończenie wszystkich wątków
        executorService.shutdown();
        if (!executorService.awaitTermination(120, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }
}
