
package edu.kdmk;

import edu.kdmk.managers.ClientManager;
import edu.kdmk.model.Client;
import edu.kdmk.repositories.EntityRepository;
import edu.kdmk.repositories.implemntations.ClientRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class ClientRepositoryTest {


    Client client1;
    EntityRepository<Client> clientRepository;
    ClientManager clientManager;
    EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
         client1 = Client.builder()
                .name("Adam A")
                .phoneNumber("123-456-789")
                .address("123 Main St")
                .build();
         emf = Persistence.createEntityManagerFactory("my-persistence-unit");
         clientRepository = new ClientRepository();
         clientManager = new ClientManager(emf, clientRepository);
    }

    @AfterEach
    public void tearDown() {
        emf.close();
    }


    @Test
    public void addClientTest() {
        clientManager.addClient(client1);
        Client client2 = clientManager.getClientById(client1.getId());
        assertEquals(client2, client1);
    }

    @Test
    public void updateClientTest() {
        clientManager.addClient(client1);
        client1.setName("Adam B");
        System.out.println(clientManager.updateClient(client1).getName());
        Client client2 = clientManager.getClientById(client1.getId());
        assertEquals(client2.getName(), "Adam B");
    }

    @Test
    public void removeClientTest() {
        clientManager.addClient(client1);
        clientManager.removeClient(client1);
        Client client2 = clientManager.getClientById(client1.getId());
        assertNull(client2);
    }

    @Test
    public void getByIdTest() {
        clientManager.addClient(client1);
        Client client2 = clientManager.getClientById(client1.getId());
        assertEquals(client2, client1);
    }

}

