package edu.kdmk.sender;

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
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RentSender {
    private final String topicName;

    private final KafkaProducer<String, String> producer;

    public RentSender(String topicName, KafkaProducer<String, String> producer) throws InterruptedException {
        this.topicName = topicName;
        this.producer = producer;

        if (isTopicExist(topicName)) {
            System.out.println("Topic " + topicName + " exists");
        } else {
            System.out.println("Topic " + topicName + " does not exist");
            createTopic();
        }
    }

    public void createTopic() throws InterruptedException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka1:9292,kafka3:9392");
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
        } catch (ExecutionException ee) {
            System.out.println(ee.getCause());
        }
    }

    public void sendRent(Rent rent) {

        RentCodec codec = new RentCodec(new ClientCodec(), new GameCodec());

        BsonDocument document = new BsonDocument();
        BsonWriter writer = new BsonDocumentWriter(document);
        codec.encode(writer, rent, EncoderContext.builder().build());

        try {
            createTopic();
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName,
                    rent.getId().toString(), document.toJson());
            Future<RecordMetadata> sent = producer.send(record);
            RecordMetadata recordMetadata = sent.get();
            System.out.println("Sender: rent sent to partition: " + recordMetadata.partition());
        } catch (InterruptedException  | ExecutionException e) {
            System.out.println(e);
        }

    }

    public boolean isTopicExist(String topicName) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka1:9292,kafka3:9392");
        try (Admin admin = Admin.create(properties)) {
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true);
            Set<String> topics = admin.listTopics(options).names().get();
            return topics.contains(topicName);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e);
            return false;
        }
    }

    public void deleteTopic(String topicName) {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka1:9292,kafka3:9392");
        try (Admin admin = Admin.create(properties)) {
            DeleteTopicsResult result = admin.deleteTopics(List.of(topicName));
            KafkaFuture<Void> futureResult = result.all();
            futureResult.get();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println(e);
        }
    }
}
