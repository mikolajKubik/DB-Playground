package edu.kdmk.model.codec;

import edu.kdmk.model.Client;
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
        String firstName = null;
        String address = null;
        String lastName = null;
        int rentalCount = 0;

        while (reader.readBsonType() != org.bson.BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            switch (fieldName) {
                case "_id":
                    id = UUID.fromString(reader.readString());
                    break;
                case "firstName":
                    firstName = reader.readString();
                    break;
                case "lastName":
                    lastName = reader.readString();
                    break;
                case "address":
                    address = reader.readString();
                    break;
                case "rentalCount":
                    rentalCount = reader.readInt32();
                    break;
                default:
                    reader.skipValue();
            }
        }

        reader.readEndDocument();

        return new Client(id, firstName, lastName, address, rentalCount); // Return a new Client with all fields
    }

    @Override
    public void encode(BsonWriter writer, Client value, EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeString("_id", value.getId().toString());
        writer.writeString("firstName", value.getFirstName());
        writer.writeString("lastName", value.getLastName());
        writer.writeString("address", value.getAddress());
        writer.writeInt32("rentalCount", value.getRentalCount());

        writer.writeEndDocument();
    }

    @Override
    public Class<Client> getEncoderClass() {
        return Client.class;
    }
}