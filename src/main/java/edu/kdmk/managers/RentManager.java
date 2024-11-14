package edu.kdmk.managers;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.Client;
import edu.kdmk.models.Rent;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.GameRepository;
import edu.kdmk.repositories.InactiveRentRepository;
import edu.kdmk.repositories.RentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentManager {
    private final MongoClient mongoClient;
    private final RentRepository rentRepository;
    private final InactiveRentRepository inactiveRentRepository;
    private final GameRepository gameRepository;
    private final ClientRepository clientRepository;

    public RentManager(MongoClient mongoClient, MongoDatabase database) {
        this.mongoClient = mongoClient;
        this.rentRepository = new RentRepository(database);
        this.gameRepository = new GameRepository(database);
        this.clientRepository = new ClientRepository(database);
        this.inactiveRentRepository = new InactiveRentRepository(database);
    }

    // Method to create a new Rent, mark Client and Game as rented
    public void createRent(Client client, Game game, LocalDate startDate, LocalDate endDate) {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            boolean markedAsRentedGame = gameRepository.markAsRented(session, game.getId());
            boolean markedAsRentedClient = clientRepository.markAsRented(session, client.getId());

            if (!markedAsRentedGame) {
                System.out.println("Game is already rented. Cannot create rent.");
                session.abortTransaction();
                return;
            }

            if (!markedAsRentedClient) {
                System.out.println("Client has reached the maximum rental limit. Cannot create rent.");
                session.abortTransaction();
                return;
            }

            Client newClient = clientRepository.findById(session, client.getId()).get();
            Game newGame = gameRepository.findById(session, game.getId());


            // Create Rent object
            Rent rent = new Rent(startDate, endDate, newClient, newGame);

            // Insert Rent into the repository
            rentRepository.insert(session, rent);

            // Mark Client and Game as rented


            session.commitTransaction();
            System.out.println("Rent created successfully and objects marked as rented.");
        } catch (Exception e) {
            System.err.println("Failed to create rent: " + e.getMessage());
        }
    }

    public Rent findRentById(UUID id) {
        try (ClientSession session = mongoClient.startSession()) {
            return rentRepository.findById(session, id);
        }
    }

    public List<Rent> getAllRents() {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            List<Rent> rents = rentRepository.findAll(session);

            session.commitTransaction();
            return rents;
        } catch (Exception e) {
            System.err.println("Failed to retrieve rents: " + e.getMessage());
            return null;
        }
    }



    public void completeRent(UUID rentId) {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            // Find the rent in the active rents collection
            Rent rent = rentRepository.findById(session, rentId);
            if (rent != null) {
                // Remove from active rent and add to inactive rent
                rentRepository.deleteById(session, rentId);
                inactiveRentRepository.insert(session, rent);

                // Unmark Client and Game as available
                gameRepository.unmarkAsRented(session, rent.getGame().getId());
                clientRepository.unmarkAsRented(session, rent.getClient().getId());
            }

            session.commitTransaction();
            System.out.println("Rent completed and moved to inactiveRents.");
        } catch (Exception e) {
            System.err.println("Failed to complete rent: " + e.getMessage());
        }
    }

    public void updateRent(Rent rent) {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            rentRepository.updateById(session, rent);

            session.commitTransaction();
            System.out.println("Rent updated successfully.");
        } catch (Exception e) {
            System.err.println("Failed to update rent: " + e.getMessage());
        }
    }



    // Method to delete a Rent, unmark Client and Game as rented
//    public void deleteRent(UUID rentId) {
//        try (ClientSession session = mongoClient.startSession()) {
//            session.startTransaction();
//
//            // Find Rent by ID
//            Rent rent = rentRepository.findById(session, rentId);
//            if (rent == null) {
//                System.out.println("Rent not found, cannot delete.");
//                return;
//            }
//
//            // Delete Rent from the repository
//            rentRepository.deleteById(session, rentId);
//
//            // Unmark Client and Game as rented
//            boolean unmarkedAsRentedGame = gameRepository.unmarkAsRented(session, rent.getGame().getId());
//            boolean unmarkedAsRentedClient = clientRepository.unmarkAsRented(session, rent.getClient().getId());
//
//            if (!unmarkedAsRentedGame) {
//                System.out.println("Game was not marked as rented. Possible inconsistency.");
//            }
//
//            if (!unmarkedAsRentedClient) {
//                System.out.println("Client was not marked as rented. Possible inconsistency.");
//            }
//
//            session.commitTransaction();
//            System.out.println("Rent deleted successfully and objects unmarked as rented.");
//        } catch (Exception e) {
//            System.err.println("Failed to delete rent: " + e.getMessage());
//        }
//    }
}
