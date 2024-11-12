package edu.kdmk.managers;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientManager {
    private final MongoClient mongoClient;
    private final ClientRepository clientRepository;

    public ClientManager(MongoClient mongoClient, MongoDatabase database) {
        this.mongoClient = mongoClient;
        this.clientRepository = new ClientRepository(database);
    }

    // Transactional insert for a new Client
    public Client insertClient(Client newClient) {
        try (ClientSession session = mongoClient.startSession()) {
            return session.withTransaction(() -> {
                //Optional<Client> retrievedClient = Optional.empty();
                if (clientRepository.insert(session, newClient)) {
                    return clientRepository.findById(session, newClient.getId());
                }
                return null;
            });
        }  catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Transactional find by UUID
    public Client findClientById(UUID id) {
        try (ClientSession session = mongoClient.startSession()) {
            return clientRepository.findById(session, id);
        }
    }

    // Transactional update of a Client, replacing the entire document
    public void updateClientById(Client updatedClient) {
        try (ClientSession session = mongoClient.startSession()) {
            session.withTransaction((TransactionBody<Void>) () -> {
                clientRepository.updateById(session, updatedClient);
                return null;
            });
        } catch (RuntimeException e) {
            System.err.println("Transaction aborted due to an error: " + e.getMessage());
        }
    }

    // Transactional deletion of a Client by UUID
    public void deleteClientById(UUID clientId) {
        try (ClientSession session = mongoClient.startSession()) {
            session.withTransaction((TransactionBody<Void>) () -> {
                clientRepository.deleteById(session, clientId);
                return null;
            });
        } catch (RuntimeException e) {
            System.err.println("Transaction aborted due to an error: " + e.getMessage());
        }
    }

    // Transactional retrieval of all clients
    public List<Client> getAllClients() {
        try (ClientSession session = mongoClient.startSession()) {
            return clientRepository.findAll(session);
        }
    }
}