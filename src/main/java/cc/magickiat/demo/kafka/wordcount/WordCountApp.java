package cc.magickiat.demo.kafka.wordcount;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Materialized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class WordCountApp {

    private static final Logger logger = LoggerFactory.getLogger(WordCountApp.class);

    public static final String INPUT_TOPIC = "topic-post";
    public static final String OUTPUT_TOPIC = "post-wordcount-output";

    public static void main(String[] args) {
        // config to connect kafka
        Properties config = getConfig();

        // streams workflow
        Topology topology = createTopology();

        // start workflow
        KafkaStreams streams = new KafkaStreams(topology, config);
        CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("shutdown-wordcount-app") {
            @Override
            public void run() {
                logger.info("##### Begin Shutdown WordCount App #####");
                latch.countDown();
                streams.close();
            }
        });

        try {
            logger.info("##### Start WordCount App #####");
            streams.start();
            latch.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public static Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        builder.<String, String>stream(INPUT_TOPIC)
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .count(Materialized.as("counts-store"))
                .toStream()
                .to(OUTPUT_TOPIC);
        return builder.build();
    }

    public static Properties getConfig() {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return config;
    }
}
