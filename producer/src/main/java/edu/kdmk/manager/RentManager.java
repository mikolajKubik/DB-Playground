package edu.kdmk.manager;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import edu.kdmk.sender.RentSender;
import edu.kdmk.model.Rent;
import edu.kdmk.repository.ClientRepository;
import edu.kdmk.repository.GameRepository;
import edu.kdmk.repository.InactiveRentRepository;
import edu.kdmk.repository.RentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RentManager {

    private final MongoClient mongoClient;
    private final RentRepository rentRepository;
    private final InactiveRentRepository inactiveRentRepository;
    private final GameRepository gameRepository;
    private final ClientRepository clientRepository;
    private final RentSender rentSender;

    public RentManager(
            MongoClient mongoClient,
            RentRepository rentRepository,
            GameRepository gameRepository,
            ClientRepository clientRepository,
            InactiveRentRepository inactiveRentRepository,
            RentSender rentSender) {
        this.mongoClient = mongoClient;
        this.rentRepository = rentRepository;
        this.gameRepository = gameRepository;
        this.clientRepository = clientRepository;
        this.inactiveRentRepository = inactiveRentRepository;
        this.rentSender = rentSender;
    }

    public boolean sendRent(Rent rent) {
        validateRent(rent);
        ClientSession session = mongoClient.startSession();
        try {
            session.startTransaction();

            boolean markedAsRentedGame = gameRepository.markAsRented(session, rent.getGame().getId());
            boolean markedAsRentedClient = clientRepository.markAsRented(session, rent.getClient().getId());

            if (!markedAsRentedGame) {
                session.abortTransaction();
                throw new IllegalStateException("Game is not available for rent.");
            }

            if (!markedAsRentedClient) {
                session.abortTransaction();
                throw new IllegalStateException("Client is not available for rent.");
            }

            rent.setRentalPrice(calculateRentPrice(rent.getStartDate(), rent.getEndDate(), rent.getGame().getPricePerDay()));

            rentSender.sendRent(rent);

        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return false;
    }


    // Method to create a new Rent, mark Client and Game as rented
    public boolean createRent(Rent rent) {
        validateRent(rent);

        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            boolean markedAsRentedGame = gameRepository.markAsRented(session, rent.getGame().getId());
            boolean markedAsRentedClient = clientRepository.markAsRented(session, rent.getClient().getId());

            if (!markedAsRentedGame) {
                session.abortTransaction();
                throw new IllegalStateException("Game is not available for rent.");
            }

            if (!markedAsRentedClient) {
                session.abortTransaction();
                throw new IllegalStateException("Client is not available for rent.");
            }

            rent.setRentalPrice(calculateRentPrice(rent.getStartDate(), rent.getEndDate(), rent.getGame().getPricePerDay()));

            // Insert Rent into the repository
            if (rentRepository.insert(session, rent)) {
                session.commitTransaction();
                return true;
            }
            session.abortTransaction();
        } catch (Exception e) {
            throw e;
        }
        return false;
    }

    public void validateRent(Rent rent) {
        if (rent.getClient() == null || rent.getGame() == null || rent.getStartDate() == null || rent.getEndDate() == null) {
            throw new IllegalArgumentException("Client, Game, Start Date and End Date are required.");
        }

        if (!isStartDateBeforeEndDate(rent.getStartDate(), rent.getEndDate())) {
            throw new IllegalArgumentException("Start Date must be before End Date.");
        }
    }

    public Optional<Rent> findRentById(UUID id) {
        try {
            return rentRepository.findById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Rent> getAllRents() {
        try {
            return rentRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }

    // Method to complete a Rent, mark Client and Game as returned
    public boolean completeRent(UUID rentId) {
        try (ClientSession session = mongoClient.startSession()) {
            session.startTransaction();

            Optional<Rent> rent = rentRepository.findById(session, rentId);
            if (rent.isPresent()) {
                boolean markedAsReturnedGame = gameRepository.unmarkAsRented(session, rent.get().getGame().getId());
                boolean markedAsReturnedClient = clientRepository.unmarkAsRented(session, rent.get().getClient().getId());

                if (!markedAsReturnedGame) {
                    session.abortTransaction();
                    throw new IllegalStateException("Game is not available for return.");
                }

                if (!markedAsReturnedClient) {
                    session.abortTransaction();
                    throw new IllegalStateException("Client is not available for return.");
                }

                rent.get().setEndDate(LocalDate.now());
                rent.get().setRentalPrice(calculateRentPrice(rent.get().getStartDate(), rent.get().getEndDate(), rent.get().getGame().getPricePerDay()));
                if (inactiveRentRepository.insert(session, rent.get()) && rentRepository.deleteById(session, rentId)) {
                    session.commitTransaction();
                    return true;
                }
            }
            session.abortTransaction();
        } catch (Exception e) {
            throw e;
        }
        return false;
    }

    public boolean updateRentalEndDate(UUID rentId, LocalDate newEndDate) {
        try {
            Optional<Rent> rent = rentRepository.findById(rentId);
            if (rent.isPresent() && isStartDateBeforeEndDate(rent.get().getStartDate(), newEndDate)) {
                rent.get().setEndDate(newEndDate);
                rent.get().setRentalPrice(calculateRentPrice(rent.get().getStartDate(), newEndDate, rent.get().getGame().getPricePerDay()));
                return rentRepository.update(rent.get());
            }
            return false;
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean isStartDateBeforeEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isBefore(endDate);
    }

    private int calculateRentPrice(LocalDate startDate, LocalDate endDate, int pricePerDay) {
        return pricePerDay * startDate.until(endDate).getDays() + pricePerDay;
    }
}
