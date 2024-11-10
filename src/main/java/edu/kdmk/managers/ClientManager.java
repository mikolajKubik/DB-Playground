package edu.kdmk.managers;

import com.mongodb.client.ClientSession;
import edu.kdmk.config.MongoDBConnection;
import edu.kdmk.models.Client;
import edu.kdmk.repositories.ClientRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientManager {
    private final MongoDBConnection mongoDBConnection;
    private final ClientRepository clientRepository;

    public void performTransaction(Client client) {
        try (ClientSession session = mongoDBConnection.startSession()) {
            session.startTransaction();  // Ensure this line works with the 5.2.0 API

            try {
                clientRepository.insertClient(session, client);

                session.commitTransaction();
                System.out.println("Transaction committed successfully.");
            } catch (Exception e) {
                session.abortTransaction();
                System.err.println("Transaction aborted due to an error: " + e.getMessage());
            }
        }
    }
}