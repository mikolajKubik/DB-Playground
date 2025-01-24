package edu.kdmk.producer;

import edu.kdmk.model.Rent;
import edu.kdmk.model.codec.ClientCodec;
import edu.kdmk.model.codec.GameCodec;
import edu.kdmk.model.codec.RentCodec;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RentProducer {
    private final String topicName;

    private KafkaProducer<String, String> producer;

    public RentProducer(String topicName) throws InterruptedException {
        this.topicName = topicName;
        createTopic();
        initProducer();
    }

    public void initProducer() {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "kafka1:9192,kafka2:9292,kafka3:9392");
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producer = new KafkaProducer<>(producerConfig);
    }

    public void createTopic() throws InterruptedException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        int partitionsNumber = 3;
        short replicationFactor = 3;
        try (Admin admin = Admin.create(properties)) {
            NewTopic newTopic = new NewTopic(topicName, partitionsNumber, replicationFactor);
            CreateTopicsOptions options = new CreateTopicsOptions()
                    .timeoutMs(1000)
                    .validateOnly(false)
                    .retryOnQuotaViolation(true);
            CreateTopicsResult result = admin.createTopics(List.of(newTopic), options);
            KafkaFuture<Void> futureResult = result.values().get(topicName);
            futureResult.get();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public void sendRent(Rent rent) {

        RentCodec codec = new RentCodec(new ClientCodec(), new GameCodec());

        BsonDocument document = new BsonDocument();
        BsonWriter writer = new BsonDocumentWriter(document);
        codec.encode(writer, rent, EncoderContext.builder().build());

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName,
                    rent.getId().toString(), document.toJson());
            Future<RecordMetadata> sent = producer.send(record);
            RecordMetadata recordMetadata = sent.get();
            System.out.println("Sender: rent sent to partition: " + recordMetadata.toString());
        } catch (InterruptedException  | ExecutionException e) {
            System.out.println(e);
        }

    }


    public void deleteTopic(String topicName) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        try (Admin admin = Admin.create(properties)) {
            DeleteTopicsResult result = admin.deleteTopics(List.of(topicName));
            KafkaFuture<Void> futureResult = result.all();
            futureResult.get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e);
        }
    }
}
