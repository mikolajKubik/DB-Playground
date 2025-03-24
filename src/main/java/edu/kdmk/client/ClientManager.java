package edu.kdmk.client;

import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientDao clientDao;

    public ClientManager(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    public void saveClient(Client client) {
        if (client == null || client.getClientId() == null || client.getFirstName() == null || client.getLastName() == null || client.getAddress() == null) {
            throw new RuntimeException();
        }

        clientDao.save(client);
    }

    public Client findClientById(UUID clientId) {
        if (clientId == null) {
            throw new RuntimeException();
        }
        return clientDao.findById(clientId);
    }

    public void delete(Client client) {
        if (client== null || client.getClientId() == null) {
            throw new RuntimeException();
        }
        clientDao.delete(client);

    }

    public void updateClient(Client client) {
        if (client == null || client.getClientId() == null) {
            throw new RuntimeException();
        }
        clientDao.update(client);
    }

    public List<Client> findByFirstName(String firstName) {
         if (firstName == null) {
            throw new RuntimeException();
        }
        return clientDao.findByFirstName(firstName);
    }
}
