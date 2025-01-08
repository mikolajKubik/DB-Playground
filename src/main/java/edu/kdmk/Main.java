package edu.kdmk;

import edu.kdmk.client.ClientMapperBuilder;
import edu.kdmk.config.CassandraConnector;
import edu.kdmk.client.Client;
import edu.kdmk.client.ClientDao;
import edu.kdmk.client.ClientMapper;
import edu.kdmk.config.CassandraSchemaCreator;
import edu.kdmk.game.GameDao;
import edu.kdmk.game.GameMapper;
import edu.kdmk.game.GameMapperBuilder;
import edu.kdmk.game.model.BoardGame;
import edu.kdmk.game.model.ComputerGame;
import edu.kdmk.game.model.GameType;
import edu.kdmk.rent.RentDao;
import edu.kdmk.rent.RentMapper;
import edu.kdmk.rent.RentMapperBuilder;
import edu.kdmk.rent.model.Rent;
import edu.kdmk.rent.model.RentByClient;
import edu.kdmk.rent.model.RentByGame;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Cassandra connection test...");

        try (CassandraConnector connector = new CassandraConnector()) {


            if (connector.getSession() != null && !connector.getSession().isClosed()) {
                System.out.println("SUCCESS: Connected to Cassandra and session is active.");
            } else {
                System.out.println("FAILURE: Session is null or closed.");
            }

            CassandraSchemaCreator schemaCreator = new CassandraSchemaCreator(connector.getSession());
//            schemaCreator.createKeyspace("rent_a_game");
            schemaCreator.createClientsTable("rent_a_game");
            schemaCreator.createGamesTable("rent_a_game");
            schemaCreator.createRentByClientTable("rent_a_game");
            schemaCreator.createRentByGameTable("rent_a_game");


            UUID uuid = UUID.randomUUID();

            Client client = new Client(uuid, "Chuj", "Jebany", "Jebalem Ci Matke 69");

            ClientMapper mapper = new ClientMapperBuilder(connector.getSession()).build();
            ClientDao clientDao = mapper.clientDao("rent_a_game", "clients");
            //ClientDao clientDao = mapper.clientDao(); // Assuming a generated implementation exists

            clientDao.save(client);
            System.out.println("Client saved successfully.");

            Client client1 = clientDao.findById(uuid);

            if (client1 != null) {
                System.out.println("Retrieved Client: " + client1.getFirstName());
            } else {
                System.out.println("Client with ID " + uuid + " not found.");
            }
            client1.setFirstName("Cwel");

            clientDao.update(client1);

            Client client2 = clientDao.findById(uuid);

            if (client2 != null) {
                System.out.println("Retrieved Client: " + client2.getFirstName());
            } else {
                System.out.println("Client with ID " + uuid + " not found.");
            }

            clientDao.delete(client2);

            Client client3 = clientDao.findById(uuid);
            if (client3 == null) {
                System.out.println("Kurwa elo dziala");
            } else {
                System.out.println("oho");
            }

            /// //////////////////////////////////////////////


            UUID bg = UUID.randomUUID();
            UUID cg = UUID.randomUUID();

            GameMapper mapperGame = new GameMapperBuilder(connector.getSession()).build();
            GameDao gameDao = mapperGame.gameDao("rent_a_game", "games");


            BoardGame boardGame = new BoardGame(bg, "Lepsza", "board_game", false, 41, 1, 4);
            ComputerGame computerGame = new ComputerGame(cg, "Fajna", "computer_game", false, 12,"PC");

            gameDao.save(boardGame);
            gameDao.save(computerGame);

            BoardGame boardGame1 = (BoardGame) gameDao.findById(bg);
            ComputerGame computerGame1 = (ComputerGame) gameDao.findById(cg);

            if (boardGame1 != null && computerGame1 != null) {
                System.out.println(boardGame1.getName() + " " + computerGame1.getName());
            }

            boardGame1.setName("Nibba");
            computerGame1.setName("Ohoho");

            gameDao.update(boardGame1);
            gameDao.update(computerGame1);

            BoardGame boardGame2 = (BoardGame) gameDao.findById(bg);
            ComputerGame computerGame2 = (ComputerGame) gameDao.findById(cg);

            if (boardGame2 != null && computerGame2 != null) {
                System.out.println(boardGame2.getName() + " " + computerGame2.getName());
            }

            gameDao.delete(boardGame2);
            gameDao.delete(computerGame2);

            BoardGame boardGame3 = (BoardGame) gameDao.findById(bg);
            ComputerGame computerGame3 = (ComputerGame) gameDao.findById(cg);

            if (boardGame3 == null && computerGame3 == null) {
                System.out.println("esz działa usuwanie");
            }

            ///    ////////////////////////////////////////////////////////////////////////////////////

            UUID clientId = UUID.randomUUID();
            UUID gameId = UUID.randomUUID();
            UUID rentId = UUID.randomUUID();

            Client clientRent = new Client(clientId, "Chuj", "Jebany", "Jebalem Ci Matke 69");
            BoardGame boardGameRent = new BoardGame(gameId, "Lepsza", "board_game", false, 41, 1, 4);

            clientDao.save(clientRent);
            gameDao.save(boardGameRent);

            RentMapper rentMapper = new RentMapperBuilder(connector.getSession()).build();
            RentDao rentDao = rentMapper.rentDao("rent_a_game");

            Rent rent = new Rent(rentId, LocalDate.now(), LocalDate.now().plusDays(9), clientId, gameId, 0);

            rentDao.save(rent);


            List<Rent> rent1 = rentDao.findByClientId(clientId);
            List<Rent> rent2 = rentDao.findByGameId(gameId);

            System.out.println(rent1.size());
            System.out.println(rent1);
            System.out.println(rent2.size());
            System.out.println(rent2);

            rent.setEndDate(LocalDate.now());
            rent.setRentalPrice(10);
            rentDao.update(rent);

            rent1 = rentDao.findByClientId(clientId);
            rent2 = rentDao.findByGameId(gameId);

            System.out.println(rent1.size());
            System.out.println(rent1);
            System.out.println(rent2.size());
            System.out.println(rent2);

            rentDao.delete(rent);

            rent1 = rentDao.findByClientId(clientId);
            rent2 = rentDao.findByGameId(gameId);

            System.out.println(rent1.size());
            System.out.println(rent1);
            System.out.println(rent2.size());
            System.out.println(rent2);

        } catch (Exception e) {
            System.err.println("FAILURE: Unable to connect to Cassandra. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}