package edu.kdmk.models.codec;

import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.models.game.GameType;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public class GameCodec implements Codec<Game> {

    @Override
    public Game decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID id = null;
        String name = null;
        GameType gameType = null;
        int pricePerDay = 0;
        int minPlayers = 0;
        int maxPlayers = 0;
        String platform = null;
        int rentalStatusCount = 0;

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            switch (fieldName) {
                case "_id":
                    id = UUID.fromString(reader.readString());
                    break;
                case "name":
                    name = reader.readString();
                    break;
                case "gameType":
                    gameType = GameType.fromString(reader.readString());
                    break;
                case "pricePerDay":
                    pricePerDay = reader.readInt32(); // Ignore for now
                    break;
                case "minPlayers":
                    minPlayers = reader.readInt32();
                    break;
                case "maxPlayers":
                    maxPlayers = reader.readInt32();
                    break;
                case "platform":
                    platform = reader.readString();
                    break;
                case "rentalStatusCount":
                    rentalStatusCount = reader.readInt32(); // Ignore for now
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.readEndDocument();

        // Use gameType to determine which subclass to return
        if (gameType == GameType.BOARD_GAME) {
            return new BoardGame(id, name, gameType, pricePerDay, rentalStatusCount, minPlayers, maxPlayers);
        } else if (gameType == GameType.COMPUTER_GAME) {
            return new ComputerGame(id, name, gameType, pricePerDay, rentalStatusCount, platform);
        } else {
            throw new IllegalArgumentException("Unknown GameType: " + gameType);
        }
    }

    @Override
    public void encode(BsonWriter writer, Game value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeString("_id", value.getId().toString());
        writer.writeString("name", value.getName());
        writer.writeString("gameType", value.getType().getTypeName()); // Serialize GameType
        writer.writeInt32("pricePerDay", value.getPricePerDay());
        writer.writeInt32("rentalStatusCount", value.getRentalStatusCount());

        if (value instanceof BoardGame boardGame) {
            writer.writeInt32("minPlayers", boardGame.getMinPlayers());
            writer.writeInt32("maxPlayers", boardGame.getMaxPlayers());
        } else if (value instanceof ComputerGame computerGame) {
            writer.writeString("platform", computerGame.getPlatform());

        }
        writer.writeEndDocument();
    }

    @Override
    public Class<Game> getEncoderClass() {
        return Game.class;
    }
}