package edu.kdmk.game;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.MapperContext;
import com.datastax.oss.driver.api.mapper.entity.EntityHelper;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.update.Update;
import edu.kdmk.game.model.Game;
import edu.kdmk.game.model.ComputerGame;
import edu.kdmk.game.model.BoardGame;
import edu.kdmk.game.model.GameType;

import java.util.UUID;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

public class GameProvider {

    private final MapperContext session;
    private final EntityHelper<Game> gameHelper;
    private final EntityHelper<ComputerGame> computerGameHelper;
    private final EntityHelper<BoardGame> boardGameHelper;

    public GameProvider(MapperContext session, EntityHelper<Game> gameHelper,
                        EntityHelper<ComputerGame> computerGameHelper,
                        EntityHelper<BoardGame> boardGameHelper) {
        this.session = session;
        this.gameHelper = gameHelper;
        this.computerGameHelper = computerGameHelper;
        this.boardGameHelper = boardGameHelper;
    }

    // Save method (Insert)
    public void save(Game game) {
        session.getSession().execute(
                switch (game.getType()) {
                    case "computer_game" -> {
                        ComputerGame computerGame = (ComputerGame) game;
                        yield session.getSession().prepare(computerGameHelper.insert().build())
                                .bind()
                                .setUuid("game_id", computerGame.getGameId())
                                .setString("name", computerGame.getName())
                                .setString("type", computerGame.getType())
                                .setBoolean("rented", computerGame.isRented()) // Specific to ComputerGam
                                .setInt("price_per_day", computerGame.getPricePerDay())
                                .setString("platform", computerGame.getPlatform());    // Specific to ComputerGame
                    }
                    case "board_game" -> {
                        BoardGame boardGame = (BoardGame) game;
                        yield session.getSession().prepare(boardGameHelper.insert().build())
                                .bind()
                                .setUuid("game_id", boardGame.getGameId())
                                .setString("name", boardGame.getName())
                                .setString("type", boardGame.getType())
                                .setBoolean("rented", boardGame.isRented()) // Specific to ComputerGam
                                .setInt("price_per_day", boardGame.getPricePerDay())
                                .setInt("min_players", boardGame.getMinPlayers()) // Specific to BoardGame
                                .setInt("max_players", boardGame.getMaxPlayers()); // Specific to BoardGame
                    }
                    default -> throw new IllegalArgumentException("Unknown game type: " + game.getType());
                }
        );
    }

    // Find by ID
    public Game findById(UUID gameId) {
        Select selectQuery = QueryBuilder
                .selectFrom("games")
                .all()
                .where(Relation.column("game_id").isEqualTo(literal(gameId)));
//                .whereColumn("id").isEqualTo(literal(gameId));

        var row = session.getSession().execute(selectQuery.build()).one();
        if (row == null) {
            return null;
        }

        String type = row.getString("type");
        return switch (type) {
            case "computer_game" -> getComputerGame(row);
            case "board_game" -> getBoardGame(row);
            default -> throw new IllegalArgumentException("Unknown game type: " + type);
        };
    }

    private ComputerGame getComputerGame(Row row) {
        return new ComputerGame(
                row.getUuid("game_id"),
                row.getString("name"),
                row.getString("type"),
                row.getBoolean("rented"),
                row.getInt("price_per_day"),
                row.getString("platform"));
    }

    private BoardGame getBoardGame(Row row) {
        return new BoardGame(
                row.getUuid("game_id"),
                row.getString("name"),
                row.getString("type"),
                row.getBoolean("rented"),
                row.getInt("price_per_day"),
                row.getInt("min_players"),
                row.getInt("max_players"));
    }

    // Delete method
    public void delete(Game game) {
        Delete deleteQuery = QueryBuilder.deleteFrom("games")
                .whereColumn("game_id").isEqualTo(literal(game.getGameId()));

        session.getSession().execute(deleteQuery.build());
    }

    // Update method
    public void update(Game game) {
        session.getSession().execute(
                switch (game.getType()) {
                    case "computer_game" -> {
                        ComputerGame computerGame = (ComputerGame) game;
                        yield session.getSession().prepare(
                                QueryBuilder.update("games")
                                        .setColumn("name", literal(computerGame.getName()))
//                                        .setColumn("type", literal(computerGame.getDescription()))
                                        .setColumn("rented", literal(computerGame.isRented()))
                                        .setColumn("price_per_day", literal(computerGame.getPricePerDay()))
                                        .setColumn("platform", literal(computerGame.getPlatform()))
                                        .whereColumn("game_id").isEqualTo(literal(computerGame.getGameId()))
                                        .build()
                        ).bind();
                    }
                    case "board_game" -> {
                        BoardGame boardGame = (BoardGame) game;
                        yield session.getSession().prepare(
                                QueryBuilder.update("games")
                                        .setColumn("name", literal(boardGame.getName()))
//                                        .setColumn("type", literal(computerGame.getDescription()))
                                        .setColumn("rented", literal(boardGame.isRented()))
                                        .setColumn("price_per_day", literal(boardGame.getPricePerDay()))
                                        .setColumn("min_players", literal(boardGame.getMinPlayers()))
                                        .setColumn("max_players", literal(boardGame.getMaxPlayers()))
                                        .whereColumn("game_id").isEqualTo(literal(boardGame.getGameId()))
                                        .build()
                        ).bind();
                    }
                    default -> throw new IllegalArgumentException("Unknown game type: " + game.getType());
                }
        );
    }
}