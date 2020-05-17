package cc.magickiat.demo.kafka.wordcount;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class WordCountAppTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, String> testInputTopic;
    private TestOutputTopic<String, Long> testOutputTopic;

    @Before
    public void setUp() throws Exception {
        Topology topology = WordCountApp.createTopology();
        Properties config = WordCountApp.getConfig();
        testDriver = new TopologyTestDriver(topology, config);

        testInputTopic = testDriver.createInputTopic(
                WordCountApp.INPUT_TOPIC,
                new Serdes.StringSerde().serializer(),
                new Serdes.StringSerde().serializer()
        );

        testOutputTopic = testDriver.createOutputTopic(
                WordCountApp.OUTPUT_TOPIC,
                new Serdes.StringSerde().deserializer(),
                new Serdes.LongSerde().deserializer()
        );


    }

    @After
    public void tearDown() throws Exception {
        testDriver.close();
    }

    @Test
    public void testWordCount(){
        testInputTopic.pipeInput("hello world");
        testInputTopic.pipeInput("hello magickiat");

        Map<String, Long> keyValues = testOutputTopic.readKeyValuesToMap();

        assertThat(keyValues.get("hello"), equalTo(2L));
        assertThat(keyValues.get("world"), equalTo(1L));
        assertThat(keyValues.get("magickiat"), equalTo(1L));
        assertTrue(testOutputTopic.isEmpty());
    }
}