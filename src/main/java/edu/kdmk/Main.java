package edu.kdmk;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.GameRepository;
import org.bson.Document;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        MongoDBConnection connection = new MongoDBConnection();

        // te walidatory trzeba wyrzucic do innej klasy jakiejs zmyslnej, moze cos przez dto mozna poddymic, idealne na pasa

        ValidationOptions gameValidationOptions = new ValidationOptions().validator(
                        Document.parse("""
                        {
                            $jsonSchema: {
                                "bsonType": "object",
                                "required": [ "price_per_day", "is_rented" ]
                                "properties": {
                                    "price_per_day": {
                                        "bsonType": "int",
                                        "minimum": 0
                                    },
                                    "is_rented": {
                                        "bsonType": "int",
                                        "minimum": 0,
                                        "maximum": 1
                                        "description": "0 - not rented, 1 - rented"
                                    }
                                }
                            }
                        }                 
                        """))
                .validationAction(ValidationAction.ERROR);
        CreateCollectionOptions gameCollectionOptions = new CreateCollectionOptions().validationOptions(gameValidationOptions);

        // ale banger, trzeba najpieerw utworzyc kolekcje korzystajac z bazy danych i dopiero ja przekazac do repo
        connection.getDatabase().createCollection("games", gameCollectionOptions);
        GameRepository gameRepository = new GameRepository(connection.getCollection("games", Game.class));




        ValidationOptions clientValidationOptions = new ValidationOptions().validator(
                        Document.parse("""
                        {
                            $jsonSchema: {
                                "bsonType": "object",
                                "required": [ "rented_games" ]
                                "properties": {
                                    "rented_games": {
                                        "bsonType": "int",
                                        "minimum": 0,
                                        "maximum": 5
                                        "description": "0 - not rented, 5 - max rented games"
                                }
                            }
                        }                 
                        """))
                .validationAction(ValidationAction.ERROR);
        CreateCollectionOptions clientCollectionOptions = new CreateCollectionOptions().validationOptions(clientValidationOptions);
        connection.getDatabase().createCollection("clients", clientCollectionOptions);
        ClientRepository clientRepository = new ClientRepository(connection.getCollection("clients", Client.class));



        ClientManager clientManager = new ClientManager(connection, clientRepository);
        GameManager gameManager = new GameManager(connection, gameRepository);

        // Create a new client instance
        Client newClient = new Client("John", "Doe", "123-456-7890", "123 Main St, Anytown, USA");


        connection.close();
    }
}