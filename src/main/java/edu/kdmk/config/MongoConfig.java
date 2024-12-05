package edu.kdmk.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import edu.kdmk.models.codec.ClientCodec;
import edu.kdmk.models.codec.GameCodec;
import edu.kdmk.models.codec.RentCodec;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.io.IOException;

public class MongoConfig implements AutoCloseable {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoConfig() {

        Dotenv dotenv = Dotenv.load();

        // Get host and port from environment variables
        String connectionString = dotenv.get("MONGO_URL"); // Default to "localhost" if not set
        String databaseName = dotenv.get("MONGO_DB"); // Default to "localhost" if not set


        // Instantiate individual codecs
        ClientCodec clientCodec = new ClientCodec();
        GameCodec gameCodec = new GameCodec();
        RentCodec rentCodec = new RentCodec(clientCodec, gameCodec);
        CodecRegistry customCodecRegistry = CodecRegistries.fromCodecs(clientCodec, gameCodec, rentCodec);

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                customCodecRegistry
        );

        // Configure MongoClient with custom codec registry
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .codecRegistry(codecRegistry)
                .build();

        // Initialize MongoClient
        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry);
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void close() throws IOException {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}