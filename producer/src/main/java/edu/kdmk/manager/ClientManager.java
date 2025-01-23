package edu.kdmk.manager;

import edu.kdmk.model.Client;
import edu.kdmk.repository.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientManager {
    private final ClientRepository clientRepository;

    public ClientManager(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean insertClient(Client newClient) {
        try {
            return clientRepository.insert(newClient);
        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<Client> findClientById(UUID id) {
        try {
            return clientRepository.findById(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean updateClient(Client updatedClient) {
        try {
            return clientRepository.update(updatedClient);
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean deleteClientById(UUID clientId) {
        try {
            return clientRepository.deleteById(clientId);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public List<Client> getAllClients() {
        try {
            return clientRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }
}