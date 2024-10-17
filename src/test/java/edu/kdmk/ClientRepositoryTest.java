
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
        Client addedClient = clientManager.addClient(client1);
        Client client2 = clientManager.getClientById(addedClient.getId());
        assertEquals(addedClient, client2);
    }

    @Test
    public void updateClientTest() {
        Client client = clientManager.addClient(client1);
        client.setName("Adam B");
        Client updatedClient = clientManager.updateClient(client);
        assertEquals(updatedClient.getName(), "Adam B");
    }

    @Test
    public void updateClientNullTest() {
        Client client = null;
        assertThrows(IllegalArgumentException.class, () -> clientManager.updateClient(client));
    }

    @Test
    public void removeClientTest() {
        Client client = clientManager.addClient(client1);
        assertTrue(clientManager.removeClient(client.getId()));
        Client client2 = clientManager.getClientById(client1.getId());
        assertNull(client2);
    }

    @Test
    public void removeClientNullTest() {
        assertThrows(IllegalArgumentException.class, () -> clientManager.removeClient(0));
    }

    @Test
    public void getByIdTest() {
        Client client = clientManager.addClient(client1);
        Client client2 = clientManager.getClientById(client.getId());
        assertEquals(client, client2);
    }
}

