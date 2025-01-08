package edu.kdmk.rent;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import edu.kdmk.rent.model.Rent;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;
import com.datastax.oss.driver.api.core.cql.BatchStatement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RentProvider {

    private final CqlSession session;

    public RentProvider(MapperContext session) {
        this.session = session.getSession();
    }

    public void save(Rent rent) {
        Insert insertRentByClient = QueryBuilder.insertInto("rent_by_client")
                .value("client_id", literal(rent.getClientId()))
                .value("start_date", literal(rent.getStartDate()))
                .value("rent_id", literal(rent.getRentId()))
                .value("end_date", literal(rent.getEndDate()))
                .value("game_id", literal(rent.getGameId()))
                .value("rental_price", literal(rent.getRentalPrice()));
//                .ifNotExists();

        Insert insertRentByGame = QueryBuilder.insertInto("rent_by_game")
                .value("game_id", literal(rent.getGameId()))
                .value("start_date", literal(rent.getStartDate()))
                .value("rent_id", literal(rent.getRentId()))
                .value("end_date", literal(rent.getEndDate()))
                .value("client_id", literal(rent.getGameId()))
                .value("rental_price", literal(rent.getRentalPrice()));
//                .ifNotExists();

        BatchStatementBuilder batchBuilder = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(insertRentByClient.build())
                .addStatement(insertRentByGame.build());

        BatchStatement batch = batchBuilder.build();

        // Execute the batch
        session.execute(batch);
    }


    public List<Rent> findByClientId(UUID clientId) {
        Select select = QueryBuilder.selectFrom("rent_by_client").all()
                .where(Relation.column("client_id").isEqualTo(literal(clientId)));
        ResultSet resultSet = session.execute(select.build());
        List<Rent> rents = new ArrayList<>();
        for (Row row : resultSet) {
            rents.add(mapRowToRent(row));
        }
        return rents;
    }

    public List<Rent> findByGameId(UUID gameId) {
        Select select = QueryBuilder.selectFrom("rent_by_game").all()
                .where(Relation.column("game_id").isEqualTo(literal(gameId)));
        ResultSet resultSet = session.execute(select.build());
        List<Rent> rents = new ArrayList<>();
        for (Row row : resultSet) {
            rents.add(mapRowToRent(row));
        }
        return rents;
    }

    private Rent mapRowToRent(Row row) {
        return new Rent(
                row.getUuid("rent_id"),
                row.getInstant("end_date").atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate(),
                row.getInstant("end_date").atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate(),
//                LocalDate.parse(row.get("start_date", LocalDateTime.class),
//                row.get("end_date", LocalDateTime.class),

//                LocalDate.parse(row.getInstant("start_date")),
//                LocalDate.parse(Objects.requireNonNull(row.getString("end_date"))),
                row.getUuid("client_id"),
                row.getUuid("game_id"),
                row.getInt("rental_price"));
    }

    public void delete(Rent rent) {
        Delete deleteRentByClient = QueryBuilder
                .deleteFrom("rent_by_client")
                .whereColumn("client_id").isEqualTo(literal(rent.getClientId()))
                .whereColumn("start_date").isEqualTo(literal(rent.getStartDate()))
                .whereColumn("rent_id").isEqualTo(literal(rent.getRentId()));

        Delete deleteRentByGame = QueryBuilder
                .deleteFrom("rent_by_game")
                .whereColumn("game_id").isEqualTo(literal(rent.getGameId()))
                .whereColumn("rent_id").isEqualTo(literal(rent.getRentId()));

        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(deleteRentByClient.build())
                .addStatement(deleteRentByGame.build())
                .build();

        session.execute(batch);
    }

    public void update(Rent rent) {
        // Build UPDATE statement for rent_by_client
        Update updateRentByClient = QueryBuilder
                .update("rent_by_client")
                .setColumn("end_date", literal(rent.getEndDate()))
                //.setColumn("game_id", literal(rent.getGameId()))
                .setColumn("rental_price", literal(rent.getRentalPrice()))
                .whereColumn("client_id").isEqualTo(literal(rent.getClientId()))
                .whereColumn("start_date").isEqualTo(literal(rent.getStartDate()))
                .whereColumn("rent_id").isEqualTo(literal(rent.getRentId()));

        // Build UPDATE statement for rent_by_game
        Update updateRentByGame = QueryBuilder
                .update("rent_by_game")
                .setColumn("end_date", literal(rent.getEndDate()))
                //.setColumn("client_id", literal(rent.getClientId()))
                .setColumn("rental_price", literal(rent.getRentalPrice()))
                .whereColumn("game_id").isEqualTo(literal(rent.getGameId()))
                .whereColumn("rent_id").isEqualTo(literal(rent.getRentId()));

        // Execute both statements in a batch
        BatchStatement batch = BatchStatement.builder(BatchType.LOGGED)
                .addStatement(updateRentByClient.build())
                .addStatement(updateRentByGame.build())
                .build();

        session.execute(batch);
    }

}
