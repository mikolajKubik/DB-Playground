docker exec -it kafka1 bash -c "kafka-consumer-groups.sh --bootstrap-server kafka1:19192 --describe --group rent-consumer"

docker exec -it kafka1 bash -c "kafka-topics.sh --describe --topic rents --bootstrap-server kafka1:19192"