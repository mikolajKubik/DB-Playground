package edu.kdmk.rent;

import edu.kdmk.game.GameDao;
import edu.kdmk.game.model.Game;
import edu.kdmk.rent.model.Rent;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RentManager {

    private final RentDao rentDao;
    private final GameDao gameDao;

    public RentManager(RentDao rentDao, GameDao gameDao) {
        this.rentDao = rentDao;
        this.gameDao = gameDao;
    }

    void saveRent(Rent rent) {
        if (rent == null || rent.getClientId() == null || rent.getGameId() == null || rent.getRentId() == null || rent.getStartDate() == null || rent.getEndDate() == null) {
            throw new RuntimeException();
        }

        if (!isStartDateBeforeEndDate(rent.getStartDate(), rent.getEndDate())) {
            throw new IllegalArgumentException();
        }

        UUID gameId = rent.getGameId();
        Game gameToUpdate = gameDao.findById(gameId);
        gameToUpdate.setRented(true);
        gameDao.update(gameToUpdate);

        rent.setRentalPrice(calculateRentPrice(rent.getStartDate(), rent.getEndDate(), gameToUpdate.getPricePerDay()));
        rentDao.save(rent);
    }

    List<Rent> findRentByClientId(UUID clientId) {
        if (clientId == null) {
            throw new RuntimeException();
        }
        return rentDao.findByClientId(clientId);
    }

    List<Rent> findRentByGameId(UUID gameId) {
        if (gameId == null) {
            throw new RuntimeException();
        }
        return rentDao.findByGameId(gameId);
    }

    void deleteRent(Rent rent) {
        if (rent == null || rent.getClientId() == null || rent.getGameId() == null || rent.getRentId() == null || rent.getStartDate() == null || rent.getEndDate() == null) {
            throw new RuntimeException();
        }
        UUID gameId = rent.getGameId();
        Game gameToUpdate = gameDao.findById(gameId);
        gameToUpdate.setRented(false);
        gameDao.update(gameToUpdate);

        rentDao.delete(rent);
    }


    void endRent(Rent rent) {
        if (rent == null || rent.getClientId() == null || rent.getGameId() == null || rent.getRentId() == null || rent.getStartDate() == null || rent.getEndDate() == null) {
            throw new RuntimeException();
        }
        Game gameToUpdate = gameDao.findById(rent.getGameId());
        gameToUpdate.setRented(false);
        gameDao.update(gameToUpdate);

        LocalDate endDate = LocalDate.now();
        rent.setEndDate(endDate);
        rent.setRentalPrice(calculateRentPrice(rent.getStartDate(), endDate, gameToUpdate.getPricePerDay()));
        rentDao.update(rent);
    }

    private boolean isStartDateBeforeEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isBefore(endDate);
    }

    private int calculateRentPrice(LocalDate startDate, LocalDate endDate, int pricePerDay) {
        return pricePerDay * startDate.until(endDate).getDays() + pricePerDay;
    }
}
