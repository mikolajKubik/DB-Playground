package edu.kdmk.rent;

import com.datastax.oss.driver.api.mapper.annotations.*;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.StatementAttributes;
import edu.kdmk.game.GameProvider;
import edu.kdmk.game.model.BoardGame;
import edu.kdmk.game.model.ComputerGame;
import edu.kdmk.game.model.Game;
import edu.kdmk.rent.model.Rent;

import java.util.List;
import java.util.UUID;

@Dao
public interface RentDao {

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    void save(Rent rent);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    List<Rent> findByClientId(UUID clientId);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    List<Rent> findByGameId(UUID gameId);

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    void delete(Rent rent);

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @QueryProvider(providerClass = RentProvider.class)
    void update(Rent rent);

}
