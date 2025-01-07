package edu.kdmk;

import com.datastax.oss.driver.api.core.CqlSession;
import edu.kdmk.config.CassandraConnector;

public class Main {

    public static void main(String[] args) {
        CassandraConnector connector = new CassandraConnector();

        // Test connection to localhost:9042
        try {
            connector.connectLocal9042();
            CqlSession session9042 = connector.getSession9042();
            if (session9042 != null && !session9042.isClosed()) {
                System.out.println("Connection to localhost:9042 was successful.");
            } else {
                System.out.println("Connection to localhost:9042 failed.");
            }
        } catch (Exception e) {
            System.out.println("Connection to localhost:9042 failed.");
            e.printStackTrace();
        }

        // Test connection to localhost:9043
        try {
            connector.connectLocal9043();
            CqlSession session9043 = connector.getSession9043();
            if (session9043 != null && !session9043.isClosed()) {
                System.out.println("Connection to localhost:9043 was successful.");
            } else {
                System.out.println("Connection to localhost:9043 failed.");
            }
        } catch (Exception e) {
            System.out.println("Connection to localhost:9043 failed.");
            e.printStackTrace();
        }

        // Optionally, close the connections after testing
        connector.close();
    }

}