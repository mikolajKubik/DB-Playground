package edu.kdmk.managers;

import com.mongodb.client.ClientSession;
import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ClientManager {
    private final MongoDBConnection mongoDBConnection;
    private final ClientRepository clientRepository;

    public Optional<Client> addNewClient(Client client) {
        var session = mongoDBConnection.startSession();
        Optional<Client> result;
        try {
            session.startTransaction();

            if (clientRepository.insertClient(session, client)) {
                result = Optional.of(client);
            } else {
                result = Optional.empty();
            }

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    public boolean removeClient(Client client) {
        var session = mongoDBConnection.startSession();
        boolean result;
        try {
            session.startTransaction();

            result = clientRepository.deleteClient(session, client);

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    public Optional<Client> getClientById(UUID id) {
        var session = mongoDBConnection.startSession();
        Optional<Client> result;
        try {
            session.startTransaction();

            result = clientRepository.getClient(id);

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }

    public Optional<Client> updateClient(Client client) {
        var session = mongoDBConnection.startSession();
        Optional<Client> result;
        try {
            session.startTransaction();

            if (clientRepository.updateClient(session, client)) {
                result = Optional.of(client);
            } else {
                result = Optional.empty();
            }

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        } finally {
            session.close();
        }
        return result;
    }
}