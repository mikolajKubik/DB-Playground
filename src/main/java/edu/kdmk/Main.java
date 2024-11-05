package edu.kdmk;

import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.models.Client;
import edu.kdmk.models.Rent;
import edu.kdmk.models.vehicle.Car;
import edu.kdmk.models.vehicle.Vehicle;
import edu.kdmk.repositories.ClientRepository;

import java.sql.Date;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {


//        Client client = new Client("Jan", "Kowalski", "123456789", "fajna 12");
//
//        Vehicle vehicle = new Car("XDD44", "BMW", "X5", 2020, "black", 200, 4, 5);
//
//        Rent rent = new Rent(Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now().plusDays(5)), 200, client, vehicle);
//
//        System.out.println(client.toString());
//
//        System.out.println(vehicle.toString());
//
//        System.out.println(rent.toString());


        try (MongoDBConnection connection = new MongoDBConnection()) {
            // Create the ClientRepository using the MongoDB connection
            ClientRepository clientRepository = new ClientRepository(connection);

            // Generate a random Client
            Client randomClient = new Client(
                    "John",
                    "Doe",
                    "123-456-7890",
                    "123 Random Street, City, Country"
            );

            // Insert the random Client into the database
            clientRepository.create(randomClient);

            // Print out the inserted Client details
            System.out.println("Inserted Client: " + randomClient);
        } catch (Exception e) {
            e.printStackTrace();
        }






        /*System.out.println("Hello world!");

        // Test the connection by attempting to start a session
        try (MongoDBConnection mongoDBConnection = new MongoDBConnection()) {
            mongoDBConnection.startSession(); // Try to start a session to test the connection
            System.out.println("Connection successful!");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
        }
        // Close the connection after testing*/
    }
}