package edu.kdmk;

import edu.kdmk.client.Client;
import edu.kdmk.client.ClientManager;
import edu.kdmk.client.ClientMapper;
import edu.kdmk.client.ClientMapperBuilder;
import edu.kdmk.config.CassandraConnector;
import edu.kdmk.config.CassandraSchemaCreator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientManagerTest {
    private static CassandraConnector connector;
    private static ClientMapper mapper;

    private Client client;
    private UUID clientId;

    @BeforeAll
    static void setup() {
        connector = new CassandraConnector();

        CassandraSchemaCreator schemaCreator = new CassandraSchemaCreator(connector.getSession());
        schemaCreator.createClientsTable("rent_a_game");
        schemaCreator.createGamesTable("rent_a_game");
        schemaCreator.createRentByClientTable("rent_a_game");
        schemaCreator.createRentByGameTable("rent_a_game");

        mapper = new ClientMapperBuilder(connector.getSession()).build();
    }

    @AfterAll
    static void tearDown(){
        try {
            connector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupClient() {
        clientId = UUID.randomUUID();
        client = new Client(clientId, "Marek", "Null", "Politechniki 12");
    }

    @Test
    void saveClientTest() {
        ClientManager clientManager = new ClientManager(mapper.clientDao("rent_a_game", "clients"));

        clientManager.saveClient(client);

        assertNotNull(clientManager.findClientById(clientId));
        assertEquals(client, clientManager.findClientById(clientId));

    }

    @Test
    void saveClientNullTest() {
        ClientManager clientManager = new ClientManager(mapper.clientDao("rent_a_game", "clients"));

        assertThrows(RuntimeException.class, () -> clientManager.saveClient(null));
    }

    @Test
    void deleteClientTest() {
        ClientManager clientManager = new ClientManager(mapper.clientDao("rent_a_game", "clients"));

        clientManager.saveClient(client);

        assertNotNull(clientManager.findClientById(clientId));
        assertEquals(client, clientManager.findClientById(clientId));

        clientManager.delete(client);

        assertNull(clientManager.findClientById(clientId));
    }

    @Test
    void deleteClientWrongIdTest() {
        ClientManager clientManager = new ClientManager(mapper.clientDao("rent_a_game", "clients"));

        clientManager.saveClient(client);

        assertNotNull(clientManager.findClientById(clientId));
        assertEquals(client, clientManager.findClientById(clientId));

        client.setClientId(UUID.randomUUID());

        clientManager.delete(client);

        assertNotNull(clientManager.findClientById(clientId));
    }

    @Test
    void updateClientTest() {
        ClientManager clientManager = new ClientManager(mapper.clientDao("rent_a_game", "clients"));

        clientManager.saveClient(client);

        assertNotNull(clientManager.findClientById(clientId));
        assertEquals(client, clientManager.findClientById(clientId));

        String firstName = clientManager.findClientById(clientId).getFirstName();

        client.setFirstName("Andrzej");

        clientManager.updateClient(client);

        assertNotNull(clientManager.findClientById(clientId));
        assertEquals(firstName, clientManager.findClientById(clientId).getFirstName());
    }
}
