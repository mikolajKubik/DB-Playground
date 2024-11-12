package edu.kdmk.models.codec;

import edu.kdmk.models.Client;
import edu.kdmk.models.Rent;
import edu.kdmk.models.game.Game;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.LocalDate;
import java.util.UUID;

public class RentCodec implements Codec<Rent> {

    private final Codec<Client> clientCodec;
    private final Codec<Game> gameCodec;

    public RentCodec(Codec<Client> clientCodec, Codec<Game> gameCodec) {
        this.clientCodec = clientCodec;
        this.gameCodec = gameCodec;
    }

    @Override
    public Rent decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID id = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        Client client = null;
        Game game = null;

        while (reader.readBsonType() != org.bson.BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            switch (fieldName) {
                case "id":
                    id = UUID.fromString(reader.readString());
                    break;
                case "startDate":
                    startDate = LocalDate.parse(reader.readString());
                    break;
                case "endDate":
                    endDate = LocalDate.parse(reader.readString());
                    break;
                case "client":
                    client = clientCodec.decode(reader, decoderContext);  // Decode embedded Client
                    break;
                case "game":
                    game = gameCodec.decode(reader, decoderContext);      // Decode embedded Game
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.readEndDocument();

        return new Rent(id, startDate, endDate, client, game);
    }

    @Override
    public void encode(BsonWriter writer, Rent value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeString("id", value.getId().toString());
        writer.writeString("startDate", value.getStartDate().toString());
        writer.writeString("endDate", value.getEndDate().toString());

        // Embed the Client and Game objects
        writer.writeName("client");
        clientCodec.encode(writer, value.getClient(), encoderContext);

        writer.writeName("game");
        gameCodec.encode(writer, value.getGame(), encoderContext);

        writer.writeEndDocument();
    }

    @Override
    public Class<Rent> getEncoderClass() {
        return Rent.class;
    }
}