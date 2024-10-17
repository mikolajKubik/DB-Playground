package edu.kdmk.managers;

import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class RentManager {

    private final EntityManagerFactory emf;
    private EntityRepository<Rent> rentRepository;
    private EntityRepository<Vehicle> vehicleRepository;
    private EntityRepository<Client> clientRepository;

    public Rent addRent(Rent rent) {
        var em = emf.createEntityManager();

        rent.setRentPrice(rent.getVehicle().getPricePerDay() *
                (int) (rent.getEndDate().toLocalDate().toEpochDay() -
                        rent.getStartDate().toLocalDate().toEpochDay()));

        try {
            em.getTransaction().begin();
            Vehicle vehicleToRent = vehicleRepository.getById(rent.getVehicle().getId(), em);
            Client clientToRent = clientRepository.getById(rent.getClient().getId(), em);

            if (vehicleToRent.getRents().isEmpty() && clientToRent.getRents().size() <= 5) {
                vehicleToRent.getRents().add(rent);
                clientToRent.getRents().add(rent);

                rentRepository.add(rent, em);

            } else {
                em.getTransaction().rollback();
                throw new IllegalArgumentException();
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return rent;
    }

    public Rent endRent(long id) {
        var em = emf.createEntityManager();
        Rent result;
        try {
            em.getTransaction().begin();
            Rent endRent = rentRepository.getById(id, em);

            if (endRent == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException();
            }

            Vehicle vehicle = rentRepository.getById(id, em).getVehicle();
            Client client = rentRepository.getById(id, em).getClient();

            vehicle.getRents().remove(endRent);
            client.getRents().remove(endRent);

            endRent.setEndDate(Date.valueOf(LocalDate.now()));
            endRent.setReturned(true);
            result = rentRepository.update(endRent, em);

            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return result;
    }

    public Rent changeRentLength(long id, int days) {
        var em = emf.createEntityManager();
        Rent result;
        try {
            em.getTransaction().begin();
            Rent rent = rentRepository.getById(id, em);

            if (rent == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException();
            }

            rent.setEndDate(Date.valueOf(rent.getEndDate().toLocalDate().plusDays(days)));
            rent.setRentPrice(rent.getVehicle().getPricePerDay() *
                    (int) (rent.getEndDate().toLocalDate().toEpochDay() -
                            rent.getStartDate().toLocalDate().toEpochDay()));
            result = rentRepository.update(rent, em);

            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return result;
    }


    public Rent getRentById(Long id) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Rent rent = rentRepository.getById(id, em);
            em.getTransaction().commit();
            return rent;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Rent> getAllRents() {
        var em = emf.createEntityManager();
        try {
            return rentRepository.getAll(em);
        } catch (Exception e) {
            throw e;
        }
    }
}
