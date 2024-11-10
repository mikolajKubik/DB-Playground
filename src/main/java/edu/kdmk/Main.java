package edu.kdmk;

import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {

        MongoDBConnection connection = new MongoDBConnection();

        ClientRepository clientRepository = new ClientRepository(connection.getCollection("clients", Client.class));

        ClientManager clientManager = new ClientManager(connection, clientRepository);

        // Create a new client instance
        Client newClient = new Client("John", "Doe", "123-456-7890", "123 Main St, Anytown, USA");

        // Perform the transaction to add the client to the database
        clientManager.performTransaction(newClient);

        connection.close();
    }
}