package edu.kdmk;

import edu.kdmk.config.KafkaConfig;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.manager.ClientManager;
import edu.kdmk.manager.GameManager;
import edu.kdmk.manager.RentManager;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.game.BoardGame;
import edu.kdmk.repository.ClientRepository;
import edu.kdmk.repository.GameRepository;
import edu.kdmk.repository.InactiveRentRepository;
import edu.kdmk.repository.RentRepository;
import edu.kdmk.producer.RentProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class KafkaConnectionTest {
    private static KafkaConfig kafkaConfig;
    private static MongoConfig mongoConfig;
    private static ClientManager clientManager;
    private static GameManager gameManager;
    private static ClientRepository clientRepository;
    private static GameRepository gameRepository;
    private static RentProducer rentProducer;
    private static RentManager rentManager;

    Client client;
    BoardGame game;

    @BeforeAll
    static void setup() throws InterruptedException {
        String connectionString = ConnectionStringProvider.getConnectionString();
        String databaseName = "ndb";

        mongoConfig = new MongoConfig(connectionString, databaseName);
        clientRepository = new ClientRepository(mongoConfig.getDatabase());
        clientManager = new ClientManager(clientRepository);

        gameRepository = new GameRepository(mongoConfig.getDatabase());
        gameManager = new GameManager(gameRepository);

        kafkaConfig = new KafkaConfig();
        rentProducer = new RentProducer("rents");

        rentManager = new RentManager(
                mongoConfig.getMongoClient(),
                new RentRepository(mongoConfig.getDatabase()),
                gameRepository,
                clientRepository,
                new InactiveRentRepository(mongoConfig.getDatabase()),
                rentProducer
        );
    }

    @AfterAll
    static void tearDown() {
        try {
            // Close MongoConfig after all tests
            if (mongoConfig != null) {
                mongoConfig.close();
            }
            if (kafkaConfig != null) {
                kafkaConfig.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sentRentTest() {

        for (int i = 0; i < 10; i++) {
            client = new Client("John", "Doe", "123456789");
            game = new BoardGame("Monopoly", 2, 4, 8);

            clientManager.insertClient(client);
            gameManager.insertGame(game);

            Rent rent = new Rent(LocalDate.now(), LocalDate.now().plusDays(7), client, game, "KDMK");

            assertTrue(rentManager.sendRent(rent));
        }
    }
}
