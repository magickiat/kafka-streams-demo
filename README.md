# Kafka Streams java application demo
WordCount application example from https://kafka.apache.org/documentation/streams/
and add some missing dependency in pom.xml combined with example in https://kafka.apache.org/25/documentation/streams/tutorial and add step in this README to run example

## Run example step (On Windows)
1. Clone project and open with favourite IDE
2. Download Kafka from https://kafka.apache.org/
3. Start zookeeper
`bin\windows\zookeeper-server-start.bat config\zookeeper.properties`
4. Start Kafka
`bin\windows\kafka-server-start.bat config\server.properties`
5. Create Kafka input topic
`bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic topic-post`
6. Create Kafka output kafka stream topic
`bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic post-wordcount-output`
7. Start console consumer
`bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic post-wordcount-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer`
8. Start Kafka Stream WordCount app from IDE
9. Start Kafka Producer
`bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic topic-post`
10. Send some text and see result in console consumer