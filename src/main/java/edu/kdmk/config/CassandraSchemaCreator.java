package edu.kdmk.config;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;


public class CassandraSchemaCreator {

    private final CqlSession session;

    public CassandraSchemaCreator(CqlSession session) {
        this.session = session;
    }

    public void createKeyspace(String keyspace) {
        CreateKeyspace keyspaceStmt = SchemaBuilder.createKeyspace(CqlIdentifier.fromCql(keyspace))
                .ifNotExists()
                .withSimpleStrategy(2)
                .withDurableWrites(true);

        session.execute(keyspaceStmt.build());
        System.out.println("Keyspace created: " + keyspace);
    }

    public void createClientsTable(String keyspace) {
        SimpleStatement createTable = SchemaBuilder.createTable(CqlIdentifier.fromCql(keyspace), CqlIdentifier.fromCql("clients"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("first_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("last_name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("address"), DataTypes.TEXT)
                .build();

        session.execute(createTable);
        System.out.println("Table created: clients");
    }

    public void createGamesTable(String keyspace) {
        SimpleStatement createTable = SchemaBuilder.createTable(CqlIdentifier.fromCql(keyspace), CqlIdentifier.fromCql("games"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("game_id"), DataTypes.UUID)
                .withColumn(CqlIdentifier.fromCql("name"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("type"), DataTypes.TEXT)
                .withColumn(CqlIdentifier.fromCql("rented"), DataTypes.BOOLEAN)
                .withColumn(CqlIdentifier.fromCql("price_per_day"), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql("min_players"), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql("max_players"), DataTypes.INT)
                .withColumn(CqlIdentifier.fromCql("platform"), DataTypes.TEXT)
                .build();

        session.execute(createTable);
        System.out.println("Table created: games");
    }

    public void createRentByClientTable(String keyspace) {
        SimpleStatement createTable = SchemaBuilder.createTable(CqlIdentifier.fromCql(keyspace), CqlIdentifier.fromCql("rent_by_client"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("client_id"), DataTypes.UUID)
                .withClusteringColumn("start_date", DataTypes.TIMESTAMP)
                .withClusteringColumn("rent_id", DataTypes.UUID)
                .withColumn("end_date", DataTypes.TIMESTAMP)
                .withColumn("game_id", DataTypes.UUID)
                .withColumn("rental_price", DataTypes.INT)
                .build();

        session.execute(createTable);
        System.out.println("Table created: rent_by_client");
    }

    public void createRentByGameTable(String keyspace) {
        SimpleStatement createTable = SchemaBuilder.createTable(CqlIdentifier.fromCql(keyspace), CqlIdentifier.fromCql("rent_by_game"))
                .ifNotExists()
                .withPartitionKey(CqlIdentifier.fromCql("game_id"), DataTypes.UUID)
                .withColumn("start_date", DataTypes.TIMESTAMP)
                .withClusteringColumn("rent_id", DataTypes.UUID)
                .withColumn("end_date", DataTypes.TIMESTAMP)
                .withColumn("client_id", DataTypes.UUID)
                .withColumn("rental_price", DataTypes.INT)
                .build();

        session.execute(createTable);
        System.out.println("Table created: rent_by_game");
    }
}