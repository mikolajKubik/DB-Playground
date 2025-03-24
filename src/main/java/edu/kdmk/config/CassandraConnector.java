package edu.kdmk.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import lombok.extern.java.Log;

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

        session.execute(getKeyspace());
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

    @Deprecated
    public SimpleStatement getClientTable() {
        return SchemaBuilder.createTable(CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)

                .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("address"), DataTypes.TEXT)
                .build();
    }
}