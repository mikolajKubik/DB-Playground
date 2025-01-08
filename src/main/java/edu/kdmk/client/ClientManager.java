package edu.kdmk.client;

import com.datastax.oss.driver.api.mapper.annotations.*;

import java.util.List;
import java.util.UUID;

public class ClientManager {

    private final ClientDao clientDao;

    ClientManager(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    void saveClient(Client client) {
        if (client == null || client.getClientId() == null || client.getFirstName() == null || client.getLastName() == null || client.getAddress() == null) {
            throw new RuntimeException();
        }

        clientDao.save(client);
    }

    Client findClientById(UUID clientId) {
        if (clientId == null) {
            throw new RuntimeException();
        }
        return clientDao.findById(clientId);
    }

    void delete(Client client) {
        if (client== null || client.getClientId() == null) {
            throw new RuntimeException();
        }
        clientDao.delete(client);

    }

    void updateClient(Client client) {
        if (client == null || client.getClientId() == null) {
            throw new RuntimeException();
        }
        clientDao.update(client);
    }

    List<Client> findByFirstName(String firstName) {
         if (firstName == null) {
            throw new RuntimeException();
        }
        return clientDao.findByFirstName(firstName);
    }
}
