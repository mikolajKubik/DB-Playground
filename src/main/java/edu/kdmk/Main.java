package edu.kdmk;

import edu.kdmk.model.Address;
import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Motorcycle;
import edu.kdmk.repositories.AddressRepository;
import edu.kdmk.repositories.ClientRepository;
import edu.kdmk.repositories.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = null;
        EntityManager entityManager = null;

        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("my-persistence-unit");
            entityManager = entityManagerFactory.createEntityManager();

            var address = Address.builder()
                    .street("Main Streettyttttttttt")
                    .city("New York")
                    .state("NY")
                    .zipCode("10044")
                    .build();

            var client = Client.builder()
                    .name("John Doeeee")
                    .phoneNumber("123-456-789")
                    .address(address)
                    .build();

            AddressRepository addressRepository = new AddressRepository(entityManager);
            ClientRepository clientRepository = new ClientRepository(entityManager);
            addressRepository.add(address);
            clientRepository.add(client);

        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
            if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
                entityManagerFactory.close();
            }
        }
    }
}