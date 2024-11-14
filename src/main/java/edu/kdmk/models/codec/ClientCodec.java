package edu.kdmk.models.codec;

import edu.kdmk.models.Client;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.UUID;

public class ClientCodec implements Codec<Client> {

    @Override
    public Client decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID id = null;
        String name = null;
        String address = null;

        while (reader.readBsonType() != org.bson.BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            switch (fieldName) {
                case "_id":
                    id = UUID.fromString(reader.readString());
                    break;
                case "name":
                    name = reader.readString();
                    break;
                case "address":
                    address = reader.readString();
                    break;
                case "rentalCount":
                    reader.readInt32(); // Ignore for now
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.readEndDocument();

        return new Client(id, name, address); // Return a new Client with all fields
    }

    @Override
    public void encode(BsonWriter writer, Client value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeString("id", value.getId().toString());
        writer.writeString("name", value.getName());
        writer.writeString("address", value.getAddress());
        writer.writeInt32("rentalCount", value.getRentalCount());

        writer.writeEndDocument();
    }

    @Override
    public Class<Client> getEncoderClass() {
        return Client.class;
    }
}