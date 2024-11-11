//package edu.kdmk.config;
//
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.session.ClientSession;
//import edu.kdmk.models.Client;
//import org.bson.Document;
//import org.bson.UuidRepresentation;
//import org.bson.codecs.configuration.CodecRegistry;
//import org.bson.codecs.pojo.PojoCodecProvider;
//
//import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
//import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
//
////
////public class MongoDBConnection implements AutoCloseable {
////    private final MongoClient mongoClient;
////    private final MongoDatabase database;
////
////    public MongoDBConnection() {
////        String connectionString = "mongodb://root:root@localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0&authSource=admin";
////
////        mongoClient = MongoClients.create(connectionString);
////        database = mongoClient.getDatabase("ndb");
////    }
////
////    public ClientSession startSession() {
////        return mongoClient.startSession();
////    }
////
////    public MongoCollection<Document> getCollection(String collectionName, Class<Client> clientClass) {
////        return database.getCollection(collectionName);
////    }
////
////    @Override
////    public void close() {
////        mongoClient.close();
////    }
////}
//public class MongoDBConnection implements AutoCloseable {
//    private final MongoClient mongoClient;
//    private final MongoDatabase database;
//
//    public MongoDBConnection() {
//        //Windows
//        //String connectionString = "mongodb://root:root@mongo1:27017,mongo2:27018,mongo3§:27019/?replicaSet=rs0&authSource=admin";
//
//        //MacOS
//        String connectionString = "mongodb://root:root@111.222.32.4:27017,111.222.32.3:27018,111.222.32.2:27019/?replicaSet=rs0&authSource=admin";
//
//
//        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
//        CodecRegistry codecRegistry = fromRegistries(
//                MongoClientSettings.getDefaultCodecRegistry(),
//                pojoCodecRegistry
//        );
//
//        // Create MongoClient settings with the custom codec registry and specify UUID representation
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .applyConnectionString(new ConnectionString(connectionString))
//                .uuidRepresentation(UuidRepresentation.STANDARD) // Specify the UUID representation
//                .codecRegistry(codecRegistry)
//                .build();
//
//        // Initialize MongoClient with settings
//        mongoClient = MongoClients.create(settings);
//        database = mongoClient.getDatabase("ndb").withCodecRegistry(codecRegistry);
//    }
//
//    public ClientSession startSession() {
//        return mongoClient.startSession();
//    }
//
//    public <T> MongoCollection<T> getCollection(String collectionName, Class<T> clazz) {
//        return database.getCollection(collectionName, clazz);
//    }
//
//    @Override
//    public void close() {
//        mongoClient.close();
//    }
//}

package edu.kdmk.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.ClientSession;  // Correct import
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnection implements AutoCloseable {
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoDBConnection() {
        // Determine the operating system
        // String os = System.getProperty("os.name").toLowerCase();
        String connectionString;


            // Windows connection string
            connectionString = "mongodb://root:root@mongo1:27017,mongo2:27018,mongo3:27019/?replicaSet=rs0&authSource=admin";

            // MacOS connection string
            //connectionString = "mongodb://root:root@111.222.32.4:27017,111.222.32.3:27018,111.222.32.2:27019/?replicaSet=rs0&authSource=admin";

            // Default or other OS fallback connection string
            //connectionString = "mongodb://root:root@localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0&authSource=admin";


        // Configure the codec registry to include the POJO codec and specify UUID representation
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry
        );

        // Create MongoClient settings with the custom codec registry and specify UUID representation
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD) // Specify the UUID representation
                .codecRegistry(codecRegistry)
                .build();

        // Initialize MongoClient with settings
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("ndb").withCodecRegistry(codecRegistry);
    }

    public ClientSession startSession() {
        return mongoClient.startSession();
    }

    public <T> MongoCollection<T> getCollection(String collectionName, Class<T> clazz) {
        return database.getCollection(collectionName, clazz);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void close() {
        mongoClient.close();
    }
}