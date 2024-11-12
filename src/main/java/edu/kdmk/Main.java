package edu.kdmk;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;
import org.bson.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        String connectionString = "mongodb://root:root@111.222.32.4:27017,111.222.32.3:27018,111.222.32.2:27019/?replicaSet=rs0&authSource=admin";
        String databaseName = "ndb";

        try (MongoConfig mongoConfig = new MongoConfig(connectionString, databaseName)) {
            GameManager gameManager = new GameManager(mongoConfig.getMongoClient(), mongoConfig.getDatabase());

            // Insert a new game
            //UUID gameId = UUID.randomUUID();
            Game newGame = new BoardGame( "Monopoly", 2, 6);
            gameManager.insertGame(newGame);

            Game ComputerGame = new ComputerGame("CS:GO", "PC");
            gameManager.insertGame(ComputerGame);

            // Find the game by ID
            Game foundGame = gameManager.findGameById(newGame.getId());
            if (foundGame != null) {
                System.out.println("Game found: " + foundGame.getName());
            }

            // Update the game by modifying its attributes and calling updateGameById
            BoardGame updatedGame = new BoardGame(newGame.getId(), "Chuj", 2, 8); // Change maxPlayers to 8
            gameManager.updateGameById(updatedGame);

            // Retrieve and display all games
            List<Game> allGames = gameManager.getAllGames();
            System.out.println("All games in database:");
            allGames.forEach(game -> System.out.println(" - " + game.getName()));

            // Delete the game by ID
            //gameManager.deleteGameById(newGame.getId());

            /////////////////////////////////////////////

            ClientManager clientManager = new ClientManager(mongoConfig.getMongoClient(), mongoConfig.getDatabase());

            // Insert a new client
            Client newClient = new Client("John Doe", "123 Main St");
            clientManager.insertClient(newClient);

            // Find the client by ID
            Client foundClient = clientManager.findClientById(newClient.getId());
            if (foundClient != null) {
                System.out.println("Client found: " + foundClient.getName());
            }

            // Update the client by modifying its attributes and calling updateClientById
            foundClient.setAddress("45669696969 Elm St");
            clientManager.updateClientById(foundClient);

            // Retrieve and display all clients
            List<Client> allClients = clientManager.getAllClients();
            System.out.println("All clients in database:");
            allClients.forEach(client -> System.out.println(" - " + client.getName()));

            // Delete the client by ID
            //clientManager.deleteClientById(newClient.getId());

            /////////////////////////////////////////////



        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}