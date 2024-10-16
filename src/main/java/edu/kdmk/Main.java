package edu.kdmk;

import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.RentManager;
import edu.kdmk.managers.VehicleManager;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.RentRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.lang.ref.Cleaner;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");

        EntityRepository<Client> clientRepository = new ClientRepository();
        EntityRepository<Vehicle> vehicleRepository = new VehicleRepository();
        EntityRepository<Rent> rentRepository = new RentRepository();

        ClientManager clientManager = new ClientManager(emf, clientRepository);
        RentManager rentManager = new RentManager(emf, rentRepository, vehicleRepository, clientRepository);
        VehicleManager vehicleManager = new VehicleManager(emf, vehicleRepository);

        emf.close();
    }
}