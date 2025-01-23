package edu.kdmk;

import edu.kdmk.model.Rent;
import edu.kdmk.model.codec.ClientCodec;
import edu.kdmk.model.codec.GameCodec;
import edu.kdmk.model.codec.RentCodec;
import edu.kdmk.repository.RentRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.text.MessageFormat;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonReader;
import org.bson.codecs.DecoderContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RentConsumer {

    RentRepository rentRepository;
    List<KafkaConsumer<UUID, String>> consumersGroup = new ArrayList<>();

    public RentConsumer(RentRepository rentRepository) {
        this.rentRepository = rentRepository;
        initConsumers();
    }

    public void closeConsumers() {
        consumersGroup.forEach(KafkaConsumer::close);
    }

    public List<KafkaConsumer<UUID, String>> getConsumersGroup() {
        return consumersGroup;
    }

    public void initConsumers() {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "rent-consumer");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka1:9292,kafka3:9392");

        for (int i = 0; i < 3; i++) {
            KafkaConsumer<UUID, String> consumer = new KafkaConsumer<>(consumerConfig);
            consumer.subscribe(List.of("rents"));
            consumersGroup.add(consumer);
        }
    }

    public void consume(KafkaConsumer<UUID, String> consumer) {
        try {
            consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
            Set<TopicPartition> assignment = consumer.assignment();
            System.out.println(consumer.groupMetadata().memberId() + " " + assignment);
            consumer.seekToBeginning(assignment);

            Duration duration = Duration.of(100, ChronoUnit.MILLIS);

            MessageFormat formatter = new MessageFormat("Consumer {5}, Topic {0}, partition {1}, offset {2, number, integer}, key {3}, value {4}");

            while (true) {
                ConsumerRecords<UUID, String> record = consumer.poll(duration);
                for (ConsumerRecord<UUID, String> rec : record) {
                    String result = formatter.format(new Object[]{
                            rec.topic(),
                            rec.partition(),
                            rec.offset(),
                            rec.key(),
                            rec.value(),
                            consumer.groupMetadata().memberId()
                    });
                    RentCodec codec = new RentCodec(new ClientCodec(), new GameCodec());
                    BsonReader reader = new BsonDocumentReader(BsonDocument.parse(rec.value()));
                    Rent rent = codec.decode(reader, DecoderContext.builder().build());

                    rentRepository.insert(rent);
                    System.out.println(result);
                    System.out.println(rentRepository.findById(rent.getId()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
