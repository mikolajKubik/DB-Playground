package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.kdmk.models.Client;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.UUID;

public class ClientRepository {
    private final MongoCollection<Client> clientCollection;

    public ClientRepository(MongoCollection<Client> clientCollection) {
        this.clientCollection = clientCollection;
    }

    public boolean insertClient(ClientSession session, Client client) {
        try {
            return clientCollection.insertOne(session, client).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean deleteClient(ClientSession session, Client client) {
        try {
            Bson filter = Filters.eq("id", client.getUuid());
            return clientCollection.deleteOne(session, filter).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }

    public Optional<Client> getClient(UUID id) {
        try {
            Bson filter = Filters.eq("id", id.toString());
            return Optional.ofNullable(clientCollection.find(filter).first());
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean updateClient(ClientSession session, Client client) {
        try {
            Bson filter = Filters.eq("id", client.getUuid());
            Bson update = Updates.combine(
                    Updates.set("firstName", client.getFirstName()),
                    Updates.set("lastName", client.getLastName()),
                    Updates.set("phoneNumber", client.getPhoneNumber()),
                    Updates.set("address", client.getAddress())
            );
            return clientCollection.updateOne(session, filter, update).wasAcknowledged();
        } catch (Exception e) {
            throw e;
        }
    }
}