package edu.kdmk.managers;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientManager {
    private final ClientRepository clientRepository;

    public ClientManager(MongoDatabase database) {
        this.clientRepository = new ClientRepository(database);
    }

    // Transactional insert for a new Client
    public boolean insertClient(Client newClient) {
        try {
            return clientRepository.insert(newClient);
        } catch (Exception e) {
            throw e;
        }
    }

    // Transactional find by UUID
    public Optional<Client> findClientById(UUID id) {
        try {
            return clientRepository.findById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    // Transactional update of a Client, replacing the entire document
    public boolean updateClient(Client updatedClient) {
        try {
            return clientRepository.update(updatedClient);
        } catch (Exception e) {
            throw e;
        }
    }

    // Transactional deletion of a Client by UUID
    public boolean deleteClientById(UUID clientId) {
        try {
            return clientRepository.deleteById(clientId);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    // Transactional retrieval of all clients
    public List<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }
}