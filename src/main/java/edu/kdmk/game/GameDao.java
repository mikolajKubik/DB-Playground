package edu.kdmk.game;

import com.datastax.oss.driver.api.mapper.annotations.*;
import edu.kdmk.game.model.BoardGame;
import edu.kdmk.game.model.ComputerGame;
import edu.kdmk.game.model.Game;

import java.util.UUID;

@Dao
public interface GameDao {

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @QueryProvider(providerClass = GameProvider.class, entityHelpers = {Game.class, ComputerGame.class, BoardGame.class})
    void save(Game game);

    @StatementAttributes(consistencyLevel = "ONE", pageSize = 100)
    @QueryProvider(providerClass = GameProvider.class, entityHelpers = {Game.class, ComputerGame.class, BoardGame.class})
    Game findById(UUID gameId);

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @QueryProvider(providerClass = GameProvider.class, entityHelpers = {Game.class, ComputerGame.class, BoardGame.class})
    void delete(Game game);

    @StatementAttributes(consistencyLevel = "TWO", pageSize = 100)
    @QueryProvider(providerClass = GameProvider.class, entityHelpers = {Game.class, ComputerGame.class, BoardGame.class})
    void update(Game game);


}
