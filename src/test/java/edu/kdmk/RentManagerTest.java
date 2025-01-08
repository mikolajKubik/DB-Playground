package edu.kdmk;

import edu.kdmk.client.Client;
import edu.kdmk.client.ClientManager;
import edu.kdmk.client.ClientMapper;
import edu.kdmk.client.ClientMapperBuilder;
import edu.kdmk.config.CassandraConnector;
import edu.kdmk.config.CassandraSchemaCreator;
import edu.kdmk.game.GameManager;
import edu.kdmk.game.GameMapper;
import edu.kdmk.game.GameMapperBuilder;
import edu.kdmk.game.model.BoardGame;
import edu.kdmk.game.model.ComputerGame;
import edu.kdmk.game.model.Game;
import edu.kdmk.rent.RentManager;
import edu.kdmk.rent.RentMapper;
import edu.kdmk.rent.RentMapperBuilder;
import edu.kdmk.rent.model.Rent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RentManagerTest {
    private static CassandraConnector connector;
    private static RentMapper rentMapper;
    private static GameMapper gameMapper;
    private static ClientMapper clientMapper;
    private static GameManager gameManager;
    private static ClientManager clientManager;

    BoardGame boardGame;
    UUID boardGameId;
    ComputerGame computerGame;
    UUID computerGameId;
    Client client;
    UUID clientId;

    @BeforeAll
    static void setup() {
        connector = new CassandraConnector();

        CassandraSchemaCreator schemaCreator = new CassandraSchemaCreator(connector.getSession());
        schemaCreator.createClientsTable("rent_a_game");
        schemaCreator.createGamesTable("rent_a_game");
        schemaCreator.createRentByClientTable("rent_a_game");
        schemaCreator.createRentByGameTable("rent_a_game");

        rentMapper = new RentMapperBuilder(connector.getSession()).build();
        gameMapper = new GameMapperBuilder(connector.getSession()).build();
        clientMapper = new ClientMapperBuilder(connector.getSession()).build();

        clientManager = new ClientManager(clientMapper.clientDao("rent_a_game", "clients"));
        gameManager = new GameManager(gameMapper.gameDao("rent_a_game", "games"));
    }

    @AfterAll
    static void tearDown() {
        try {
            connector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupRent() {
        boardGameId = UUID.randomUUID();
        computerGameId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        client = new Client(clientId, "Marek", "Null", "Politechniki 12");
        computerGame = new ComputerGame(computerGameId, "Mortal Kombat", "computer_game", false, 5, "PC");
        boardGame = new BoardGame(boardGameId, "Monopoly", "board_game", false, 10, 2, 6);

        clientManager.saveClient(client);
        gameManager.saveGame(computerGame);
        gameManager.saveGame(boardGame);
    }

    @Test
    void saveRentTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao("rent_a_game"), gameMapper.gameDao("rent_a_game", "games"));

        Rent rentComputerGame = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(9), clientId, computerGameId, 0);
        Rent rentBoardGame = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(10), clientId, boardGameId, 0);


    }

    @Test
    void saveValidRentTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(5), clientId, computerGameId, 0);

        rentManager.saveRent(rent);

        List<Rent> rents = rentManager.findRentByClientId(clientId);
        assertEquals(1, rents.size());
        assertEquals(rent.getRentId(), rents.get(0).getRentId());
    }

    @Test
    void invalidStartDateEndDateTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now().plusDays(5), LocalDate.now(), clientId, computerGameId, 0);

        assertThrows(IllegalArgumentException.class, () -> rentManager.saveRent(rent));
//        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    void rentAlreadyRentedGameTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(5), clientId, computerGameId, 0);
        rentManager.saveRent(rent);

        Rent overlappingRent = new Rent(UUID.randomUUID(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(6), clientId, computerGameId, 0);
        assertThrows(RuntimeException.class, () -> rentManager.saveRent(overlappingRent));
    //    assertEquals("Game is already rented", exception.getMessage());
    }

    @Test
    void saveRentWithNullRentIdTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
        Rent rent = new Rent(null, LocalDate.now(), LocalDate.now().plusDays(5), clientId, computerGameId, 0);

        assertThrows(RuntimeException.class, () -> rentManager.saveRent(rent));
//        assertEquals("Invalid rent data", exception.getMessage());
    }

    @Test
    void deleteValidRentTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(5), clientId, computerGameId, 0);

        rentManager.saveRent(rent);
        rentManager.deleteRent(rent);

        List<Rent> rents = rentManager.findRentByClientId(clientId);
        assertTrue(rents.isEmpty());
    }

//    @Test
//    void deleteNonExistentRentTest() {
//        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
//        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(5), clientId, computerGameId, 0);
//
//        assertThrows(RuntimeException.class, () -> rentManager.deleteRent(rent));
//      //  assertEquals("Rent not found", exception.getMessage());
//    }

    @Test
    void endRentTest() {
        RentManager rentManager = new RentManager(rentMapper.rentDao(), gameMapper.gameDao());
        Rent rent = new Rent(UUID.randomUUID(), LocalDate.now(), LocalDate.now().plusDays(5), clientId, computerGameId, 0);

        rentManager.saveRent(rent);
        rentManager.endRent(rent);

        Game game = gameMapper.gameDao().findById(computerGameId);
        assertFalse(game.isRented());

//        Rent updatedRent = rentMapper.rentDao().(rent.getRentId());
//        assertEquals(LocalDate.now(), updatedRent.getEndDate());
    }




}
