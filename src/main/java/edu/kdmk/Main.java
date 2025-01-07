package edu.kdmk;

import edu.kdmk.client.ClientMapperBuilder;
import edu.kdmk.config.CassandraConnector;
import edu.kdmk.client.Client;
import edu.kdmk.client.ClientDao;
import edu.kdmk.client.ClientMapper;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Cassandra connection test...");

        try (CassandraConnector connector = new CassandraConnector()) {
            if (connector.getSession() != null && !connector.getSession().isClosed()) {
                System.out.println("SUCCESS: Connected to Cassandra and session is active.");
            } else {
                System.out.println("FAILURE: Session is null or closed.");
            }

            UUID uuid = UUID.randomUUID();

            Client client = new Client(uuid, "Chuj", "Jebany", "Jebalem Ci Matke 69");

            ClientMapper mapper = new ClientMapperBuilder(connector.getSession()).build();
            ClientDao clientDao = mapper.clientDao("rent_a_game", "clients");
            //ClientDao clientDao = mapper.clientDao(); // Assuming a generated implementation exists

            clientDao.save(client);
            System.out.println("Client saved successfully.");

            Client client1 = clientDao.findById(uuid);

            if (client1 != null) {
                System.out.println("Retrieved Client: " + client1.getFirstName());
            } else {
                System.out.println("Client with ID " + uuid + " not found.");
            }
            client1.setFirstName("Cwel");

            clientDao.update(client1);

            Client client2 = clientDao.findById(uuid);

            if (client2 != null) {
                System.out.println("Retrieved Client: " + client2.getFirstName());
            } else {
                System.out.println("Client with ID " + uuid + " not found.");
            }

            clientDao.delete(client2);

            Client client3 = clientDao.findById(uuid);
            if (client3 == null) {
                System.out.println("Kurwa elo dziala");
            } else {
                System.out.println("oho");
            }


        } catch (Exception e) {
            System.err.println("FAILURE: Unable to connect to Cassandra. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}