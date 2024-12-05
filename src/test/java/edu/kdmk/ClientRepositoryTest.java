package edu.kdmk;

import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.codec.ClientCodec;
import edu.kdmk.repositories.ClientRepository;
import org.bson.*;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientRepositoryTest {
    private static MongoConfig mongoConfig;
    Client client;

    @BeforeAll
    static void setup() {
        mongoConfig = new MongoConfig();
    }

    @AfterAll
    static void tearDown() {
        try {
            if (mongoConfig != null) {
                mongoConfig.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setupClient() {
        client = new Client(UUID.randomUUID(), "Ziut", "Deer", "123 Main St", 0);
    }

    @Test
    void insertClientTest() {
        ClientManager clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertInstanceOf(Client.class, clientManager.findClientById(client.getId()).get());
    }

    @Test
    void deleteClientPositiveTest() {
        ClientManager clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));

        assertTrue(clientManager.insertClient(client));

        assertTrue(clientManager.deleteClientById(client.getId()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());
    }

    @Test
    void deleteNonExistingClientTest() {
        ClientManager clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));

        assertTrue(clientManager.findClientById(client.getId()).isEmpty());

        assertTrue(clientManager.deleteClientById(client.getId()));
    }

    @Test
    void updateClientTest() {
        ClientManager clientManager = new ClientManager(new ClientRepository(mongoConfig.getDatabase()));

        assertTrue(clientManager.insertClient(client));

        client.setFirstName("Ziut2");

        assertTrue(clientManager.updateClient(client));

        assertTrue(clientManager.findClientById(client.getId()).isPresent());
        assertEquals(client.getFirstName(), clientManager.findClientById(client.getId()).get().getFirstName());
    }

    @Test
    void encodeTest() {
        ClientCodec codec = new ClientCodec();

        BsonDocument document = new BsonDocument();
        BsonWriter writer = new BsonDocumentWriter(document);
        codec.encode(writer, client, EncoderContext.builder().build());

        assertEquals(client.getId().toString(), document.getString("_id").getValue());
        assertEquals(client.getFirstName(), document.getString("firstName").getValue());
        assertEquals(client.getLastName(), document.getString("lastName").getValue());
        assertEquals(client.getAddress(), document.getString("address").getValue());
        assertEquals(0, document.getInt32("rentalCount").getValue());
    }

    @Test
    void decodeTest() {
        ClientCodec codec = new ClientCodec();

        BsonDocument document = new BsonDocument()
                .append("_id", new BsonString(client.getId().toString()))
                .append("firstName", new BsonString(client.getFirstName()))
                .append("lastName", new BsonString(client.getLastName()))
                .append("address", new BsonString(client.getAddress()));

        BsonReader reader = new BsonDocumentReader(document);
        Client decodedClient = codec.decode(reader, DecoderContext.builder().build());

        assertEquals(client.getId(), decodedClient.getId());
        assertEquals(client.getFirstName(), decodedClient.getFirstName());
        assertEquals(client.getLastName(), decodedClient.getLastName());
        assertEquals(client.getAddress(), decodedClient.getAddress());
        assertEquals(client.getRentalCount(), decodedClient.getRentalCount());
    }
}
