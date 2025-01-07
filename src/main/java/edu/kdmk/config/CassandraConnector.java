package edu.kdmk.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Log
public class CassandraConnector {

    /**
     * Primary session (for localhost:9042 by default).
     */
    private CqlSession session9042;

    /**
     * Secondary session (for localhost:9043 by default).
     */
    private CqlSession session9043;

    /**
     * Creates a new connection to a Cassandra node on the given host/port,
     * using the provided datacenter name and credentials.
     */
    public CqlSession connect(String node, int port, String datacenter, String username, String password) {
        try {
            CqlSessionBuilder builder = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(node, port))
                    .withLocalDatacenter(datacenter)
                    .withAuthCredentials(username, password);
//                  .withKeyspace("my keyspace");
            CqlSession newSession = builder.build();
//            log.info("Connected to Cassandra at {}:{}", node, port);
            return newSession;

        } catch (Exception e) {
//            log.error("Error connecting to Cassandra at {}:{}", node, port, e);
            throw new RuntimeException("Unable to connect to Cassandra", e);
        }
    }

    /**
     * Convenience method to connect to localhost:9042
     * (default datacenter: datacenter1, credentials: cassandra/cassandrapassword).
     */
    public void connectLocal9042() {
        if (session9042 != null && !session9042.isClosed()) {
            log.info("Session to localhost:9042 is already open.");
            return;
        }
        session9042 = connect("127.0.0.1", 9042, "datacenter1", "cassandra", "cassandrapassword");
    }

    /**
     * Convenience method to connect to localhost:9043
     * (default datacenter: datacenter1, credentials: cassandra/cassandrapassword).
     */
    public void connectLocal9043() {
        if (session9043 != null && !session9043.isClosed()) {
            log.info("Session to localhost:9043 is already open.");
            return;
        }
        session9043 = connect("127.0.0.1", 9043, "datacenter1", "cassandra", "cassandrapassword");
    }

    /**
     * Closes both sessions, if open.
     */
    public void close() {
        if (session9042 != null && !session9042.isClosed()) {
            session9042.close();
            log.info("Cassandra session for port 9042 closed.");
        }
        if (session9043 != null && !session9043.isClosed()) {
            session9043.close();
            log.info("Cassandra session for port 9043 closed.");
        }
    }

    public CqlSession getSession9042() {
        return session9042;
    }

    public CqlSession getSession9043() {
        return session9043;
    }
}