package edu.kdmk.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.models.Client;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Optional;
import java.util.UUID;
//
//public class ClientRepository {
//
////    private final MongoDBConnection connection;
//    private final MongoCollection<Document> collection;
//
//    public ClientRepository(MongoDBConnection connection) {
////        this.connection = connection;
//        this.collection = connection.getCollection("clients", Client.class);
//    }
//
//    public void create(Client client) {
//        /*Document document = new Document()
//                .append("_id", client.getUuid().toString())
//                .append("first_name", client.getFirstName())
//                .append("last_name", client.getLastName())
//                .append("phone_number", client.getPhoneNumber())
//                .append("address", client.getAddress());*/
//
//        collection.insertOne(client);
//    }
//
//    public Optional<Client> read(UUID uuid) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        Document document = collection.find(filter).first();
//
//        if (document != null) {
//            Client client = new Client();
//            client.setUuid(UUID.fromString(document.getString("_id")));
//            client.setFirstName(document.getString("first_name"));
//            client.setLastName(document.getString("last_name"));
//            client.setPhoneNumber(document.getString("phone_number"));
//            client.setAddress(document.getString("address"));
//            return Optional.of(client);
//        }
//        return Optional.empty();
//    }
//
//    public void update(UUID uuid, Client updatedClient) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        Bson updates = Updates.combine(
//                Updates.set("first_name", updatedClient.getFirstName()),
//                Updates.set("last_name", updatedClient.getLastName()),
//                Updates.set("phone_number", updatedClient.getPhoneNumber()),
//                Updates.set("address", updatedClient.getAddress())
//        );
//
//        collection.updateOne(filter, updates);
//    }
//
//    public void delete(UUID uuid) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        collection.deleteOne(filter);
//    }
//}
public class ClientRepository {
    private final MongoCollection<Client> clientCollection;

    public ClientRepository(MongoCollection<Client> clientCollection) {
        this.clientCollection = clientCollection;
    }

    public void insertClient(ClientSession session, Client client) {
        if (session != null) {
            clientCollection.insertOne(session, client);
        } else {
            clientCollection.insertOne(client);
        }
    }

//    private final MongoCollection<Client> collection;
//
//    public ClientRepository(MongoDBConnection connection) {
//        this.collection = connection.getCollection("clients", Client.class);
//    }
//
//    // Create method
//    public void create(Client client) {
//        collection.insertOne(client);
//    }
//
//    // Read method by UUID
//    public Optional<Client> read(UUID uuid) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        Client client = collection.find(filter).first();
//        return Optional.ofNullable(client);
//    }
//
//    // Update method
//    public void update(UUID uuid, Client updatedClient) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        collection.replaceOne(filter, updatedClient);
//    }
//
//    // Delete method
//    public void delete(UUID uuid) {
//        Bson filter = Filters.eq("_id", uuid.toString());
//        collection.deleteOne(filter);
//    }
}