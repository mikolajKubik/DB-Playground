package edu.kdmk;

import edu.kdmk.model.Client;
import edu.kdmk.model.Rent;
import edu.kdmk.model.vehicle.Car;
import edu.kdmk.model.vehicle.Vehicle;
import edu.kdmk.repositories.JPAUtil;
import edu.kdmk.repositories.implemntations.ClientRepository;
import edu.kdmk.repositories.implemntations.RentRepository;
import edu.kdmk.repositories.implemntations.VehicleRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ClientRepositoryTest {

    Client client1;

    @BeforeEach
    public void setUp() {
         client1 = Client.builder()
                .name("Adam A")
                .phoneNumber("123-456-789")
                .address("123 Main St")
                .build();
    }

    @Test
    public void addClientTest() {
        ClientRepository clientRepository = new ClientRepository();
        clientRepository.add(client1);
        Client client = clientRepository.getById(client1.getId());
        assertEquals(client, client1);
    }

    @Test
    public void updateClientTest() {
        ClientRepository clientRepository = new ClientRepository();
        clientRepository.add(client1);
        client1.setName("Adam B");
        clientRepository.update(client1);
        Client client2 = clientRepository.getById(client1.getId());
        assertEquals(client2.getName(), "Adam B");
    }

    @Test
    public void removeClientTest() {
        ClientRepository clientRepository = new ClientRepository();
        clientRepository.add(client1);
        var result = clientRepository.remove(client1);
        assertTrue(result);

        Client client = clientRepository.getById(client1.getId());
        assertNull(client);
    }

    @Test
    public void getByIdTest() {
        ClientRepository clientRepository = new ClientRepository();
        clientRepository.add(client1);
        Client client = clientRepository.getById(client1.getId());
        assertEquals(client, client1);
    }

}
