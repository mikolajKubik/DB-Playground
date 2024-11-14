package edu.kdmk;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.managers.RentManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        String connectionString = "mongodb://root:root@111.222.32.4:27017,111.222.32.3:27018,111.222.32.2:27019/?replicaSet=rs0&authSource=admin";
        String databaseName = "ndb";

        try (MongoConfig mongoConfig = new MongoConfig(connectionString, databaseName)) {
            GameManager gameManager = new GameManager(mongoConfig.getDatabase());

            // Insert a new game
            //UUID gameId = UUID.randomUUID();
            Game newGame = new BoardGame( "Monopoly", 2, 6);
            gameManager.insertGame(newGame);

            Game ComputerGame = new ComputerGame("CS:GO", "PC");
            gameManager.insertGame(ComputerGame);

            // Find the game by ID
            Optional<Game> foundGame = gameManager.findGameById(newGame.getId());
            if (foundGame.isPresent()) {
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAGame found: " + foundGame.get().getName());
                // Update the game by modifying its attributes and calling updateGameById
                BoardGame updatedGame = new BoardGame(newGame.getId(), "FajnaGra", 2, 8); // Change maxPlayers to 8
                gameManager.updateGame(updatedGame);
            }

            // Retrieve and display all games
            List<Game> allGames = gameManager.getAllGames();
            System.out.println("All games in database:");
            allGames.forEach(game -> System.out.println(" - " + game.getName()));

            // Delete the game by ID
            //gameManager.deleteGameById(newGame.getId());

            /////////////////////////////////////////////

            ClientManager clientManager = new ClientManager(mongoConfig.getDatabase());

            // Insert a new client
            Client newClient = new Client("John", "Doe", "123 Main St");
            clientManager.insertClient(newClient);

            // Find the client by ID
            Optional<Client> foundClient = clientManager.findClientById(newClient.getId());
            if (foundClient.isPresent()) {
                System.out.println("Client found: " + foundClient.get().getFirstName());

                // Update the client by modifying its attributes and calling updateClientById
                foundClient.get().setAddress("45669696969 Elm St");
                clientManager.updateClient(foundClient.get());
            }


            // Retrieve and display all clients
            List<Client> allClients = clientManager.getAllClients();
            System.out.println("All clients in database:");
            allClients.forEach(client -> System.out.println(" - " + client.getFirstName()));

            // Delete the client by ID
            //clientManager.deleteClientById(newClient.getId());

            /////////////////////////////////////////////


            RentManager rentManager = new RentManager(mongoConfig.getMongoClient(), mongoConfig.getDatabase());

            // Create a new Client
            Client client = new Client("John", "Fah", "123 Main St");
            System.out.println("Client created: " + client.getFirstName());
            clientManager.insertClient(client);

            // Create a new Game
            Game game = new BoardGame("Monopoly", 2, 6);
            System.out.println("Game created: " + game.getName());
            gameManager.insertGame(game);

            // Create a single Rent
            System.out.println("Creating a single rent...");
            rentManager.createRent(client, game, LocalDate.now(), LocalDate.now().plusDays(7));
            System.out.println("Single rent created successfully.");


            rentManager.createRent(newClient, game, LocalDate.now(), LocalDate.now().plusDays(7));


            /////////////////////////////////////////////
            //System.out.println(rentManager.findRentById(UUID.fromString("7fe77754-1489-45fe-884f-6afa312a7ca2")).getId());
            //rentManager.completeRent(rentManager.findRentById(UUID.fromString("7fe77754-1489-45fe-884f-6afa312a7ca2")).getId());

            System.out.println(rentManager.findRentById(UUID.fromString("d104b1c2-27b9-46d2-990f-27f74cc8a85f")).getClient().toString());
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}