package edu.kdmk;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.GameRepository;
import org.bson.Document;

import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {


        try (MongoDBConnection mongoDBConnection = new MongoDBConnection()) {
            GameRepository gameRepository = new GameRepository(mongoDBConnection.getCollection("games", Game.class));
            GameManager gameManager = new GameManager(mongoDBConnection, gameRepository);

            // Step 1: Create a new ComputerGame object
            ComputerGame game = new ComputerGame(
                    "Example Game",     // gameName
                    12,                 // recommendedAge
                    2021,               // releaseYear
                    "Example Publisher",// publisher
                    15,                 // pricePerDay
                    "PC",               // platform
                    true                // isMultiplayer
            );

            // Step 2: Insert the game into MongoDB
            Optional<Game> insertedGame = gameManager.insertGame(game);
            if (insertedGame.isPresent()) {
                System.out.println("Game successfully inserted: " + insertedGame.get());
            } else {
                System.out.println("Failed to insert the game.");
            }
            System.out.println("Game successfully inserted: " + insertedGame.get().getUuid());

            // Step 3: Retrieve the game from MongoDB using its ID
            UUID gameId = game.getUuid();
            System.out.println("Retrieving game with ID: " + gameId);
            Optional<Game> retrievedGame = gameManager.getGameById(gameId);
            //System.out.println("Retrieved game: " + retrievedGame.get().getUuid());
            // Step 4: Print the retrieved game
            if (retrievedGame.isPresent()) {
                System.out.println("Game successfully retrieved: " + retrievedGame.get());
            } else {
                System.out.println("Game not found in the database.");
            }



            ClientRepository clientRepository = new ClientRepository(mongoDBConnection.getCollection("clients", Client.class));
            ClientManager clientManager = new ClientManager(mongoDBConnection, clientRepository);

            // Step 1: Create a new client instance
            Client newClient = new Client("John", "Doe", "123-456-7890", "123 Main St, Anytown, USA");

            // Step 2: Insert the client into MongoDB
            Optional<Client> insertedClient = clientManager.addNewClient(newClient);
            if (insertedClient.isPresent()) {
                System.out.println("Client successfully inserted: " + insertedClient.get());
            } else {
                System.out.println("Failed to insert the client.");
            }

            // Step 3: Retrieve the client from MongoDB using its ID
            UUID clientId = newClient.getUuid();
            System.out.println("Retrieving client with ID: " + clientId);
            Optional<Client> retrievedClient = clientManager.getClientById(clientId);
            // Step 4: Print the retrieved client
            if (retrievedClient.isPresent()) {
                System.out.println("Client successfully retrieved: " + retrievedClient.get());
            } else {
                System.out.println("Client not found in the database.");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        ////////////

//        MongoDBConnection connection = new MongoDBConnection();
//
//        // te walidatory trzeba wyrzucic do innej klasy jakiejs zmyslnej, moze cos przez dto mozna poddymic, idealne na pasa
//
//        ValidationOptions gameValidationOptions = new ValidationOptions().validator(
//                        Document.parse("""
//                        {
//                            $jsonSchema: {
//                                "bsonType": "object",
//                                "required": [ "price_per_day", "is_rented" ]
//                                "properties": {
//                                    "price_per_day": {
//                                        "bsonType": "int",
//                                        "minimum": 0
//                                    },
//                                    "is_rented": {
//                                        "bsonType": "int",
//                                        "minimum": 0,
//                                        "maximum": 1
//                                        "description": "0 - not rented, 1 - rented"
//                                    }
//                                }
//                            }
//                        }
//                        """))
//                .validationAction(ValidationAction.ERROR);
//        CreateCollectionOptions gameCollectionOptions = new CreateCollectionOptions().validationOptions(gameValidationOptions);
//
//        // ale banger, trzeba najpieerw utworzyc kolekcje korzystajac z bazy danych i dopiero ja przekazac do repo
//        connection.getDatabase().createCollection("games", gameCollectionOptions);
//        GameRepository gameRepository = new GameRepository(connection.getCollection("games", Game.class));
//
//
//
//
//        ValidationOptions clientValidationOptions = new ValidationOptions().validator(
//                        Document.parse("""
//                        {
//                            $jsonSchema: {
//                                "bsonType": "object",
//                                "required": [ "rented_games" ]
//                                "properties": {
//                                    "rented_games": {
//                                        "bsonType": "int",
//                                        "minimum": 0,
//                                        "maximum": 5
//                                        "description": "0 - not rented, 5 - max rented games"
//                                }
//                            }
//                        }
//                        """))
//                .validationAction(ValidationAction.ERROR);
//        CreateCollectionOptions clientCollectionOptions = new CreateCollectionOptions().validationOptions(clientValidationOptions);
//        connection.getDatabase().createCollection("clients", clientCollectionOptions);
//        ClientRepository clientRepository = new ClientRepository(connection.getCollection("clients", Client.class));
//
//
//
//        ClientManager clientManager = new ClientManager(connection, clientRepository);
//        GameManager gameManager = new GameManager(connection, gameRepository);
//
//        // Create a new client instance
//        Client newClient = new Client("John", "Doe", "123-456-7890", "123 Main St, Anytown, USA");
//
//
//        connection.close();
//

        //////////////////////////


    }
}