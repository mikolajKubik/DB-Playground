package edu.kdmk.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Log
public class CassandraConnector implements AutoCloseable {

    private CqlSession session;

    public CassandraConnector() {
        connect();
    }

    public void connect() {
        session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
                .addContactPoint(new InetSocketAddress("127.0.0.1", 9043))
                .withLocalDatacenter("dc1")
                .withAuthCredentials("cassandra", "cassandrapassword")
                // Wykomentowany kod jest odpowiedzialny za polecenie USE rent_a_game.
                // Nie trzeba będzie pozniej podawac keyspace, ale na poczatku on nie istnieje
                // i trzeba go utworzyć
                .withKeyspace(CqlIdentifier.fromCql("rent_a_game"))
                .build();
/*
        CreateKeyspace keyspace = SchemaBuilder.createKeyspace(CqlIdentifier.fromCql("rent_a_game"))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        SimpleStatement createKeyspace = keyspace.build();*/

        session.execute(getKeyspace());
        session.execute(getClientTable());
    }

    public CqlSession getSession() {
        return session;
    }

    @Override
    public void close() throws  Exception {
        if (session != null) {
            session.close();
        }
    }

    public SimpleStatement getKeyspace() {
        CreateKeyspace keyspace = SchemaBuilder.createKeyspace(CqlIdentifier.fromCql("rent_a_game"))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);
        return keyspace.build();
    }

    public SimpleStatement getClientTable() {
        return SchemaBuilder.createTable(CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)

                .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("address"), DataTypes.TEXT)
                .build();
    }



//    /**
//     * Primary session (for localhost:9042 by default).
//     */
//    private CqlSession session9042;
//
//    /**
//     * Secondary session (for localhost:9043 by default).
//     */
//    private CqlSession session9043;
//
//    /**
//     * Creates a new connection to a Cassandra node on the given host/port,
//     * using the provided datacenter name and credentials.
//     */
//    public CqlSession connect(String node, int port, String datacenter, String username, String password) {
//        try {
//            CqlSessionBuilder builder = CqlSession.builder()
//                    .addContactPoint(new InetSocketAddress(node, port))
//                    .withLocalDatacenter(datacenter)
//                    .withAuthCredentials(username, password);
////                  .withKeyspace("my keyspace");
//            CqlSession newSession = builder.build();
////            log.info("Connected to Cassandra at {}:{}", node, port);
//            return newSession;
//
//        } catch (Exception e) {
////            log.error("Error connecting to Cassandra at {}:{}", node, port, e);
//            throw new RuntimeException("Unable to connect to Cassandra", e);
//        }
//    }
//
//    /**
//     * Convenience method to connect to localhost:9042
//     * (default datacenter: datacenter1, credentials: cassandra/cassandrapassword).
//     */
//    public void connectLocal9042() {
//        if (session9042 != null && !session9042.isClosed()) {
//            log.info("Session to localhost:9042 is already open.");
//            return;
//        }
//        session9042 = connect("127.0.0.1", 9042, "datacenter1", "cassandra", "cassandrapassword");
//    }
//
//    /**
//     * Convenience method to connect to localhost:9043
//     * (default datacenter: datacenter1, credentials: cassandra/cassandrapassword).
//     */
//    public void connectLocal9043() {
//        if (session9043 != null && !session9043.isClosed()) {
//            log.info("Session to localhost:9043 is already open.");
//            return;
//        }
//        session9043 = connect("127.0.0.1", 9043, "datacenter1", "cassandra", "cassandrapassword");
//    }
//
//    /**
//     * Closes both sessions, if open.
//     */
//    public void close() {
//        if (session9042 != null && !session9042.isClosed()) {
//            session9042.close();
//            log.info("Cassandra session for port 9042 closed.");
//        }
//        if (session9043 != null && !session9043.isClosed()) {
//            session9043.close();
//            log.info("Cassandra session for port 9043 closed.");
//        }
//    }
//
//    public CqlSession getSession9042() {
//        return session9042;
//    }
//
//    public CqlSession getSession9043() {
//        return session9043;
//    }
}