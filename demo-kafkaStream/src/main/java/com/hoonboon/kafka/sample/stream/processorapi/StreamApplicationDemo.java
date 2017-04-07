package com.hoonboon.kafka.sample.stream.processorapi;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.StateStoreSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StreamApplicationDemo {

	private static final Logger log = LoggerFactory.getLogger(StreamApplicationDemo.class);
	
	static final String hostIp = "10.200.10.1";
	
	public static final String STATE_STORE_COUNT = "Counts";
	
	StateStoreSupplier countStore;
	
	KafkaStreams streams;
	
	boolean isShutdown = false;
	
	public StreamApplicationDemo() {
		init();
	}
	
	void init() {
		
		countStore = Stores.create(STATE_STORE_COUNT)
				.withKeys(Serdes.String())
				.withValues(Serdes.Long())
				.persistent()
				.build();
		
		final Thread mainThread = Thread.currentThread();
		
		// Registering a shutdown hook so we can exit cleanly
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Starting exit...");
				// Note that shutdownhook runs in a separate thread
				isShutdown = true;

				// Stop the Kafka Streams instance
				streams.close();
				
				try {
					mainThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		// Use the builders to define the actual processing topology, e.g. to specify
		// from which input topics to read, which stream operations (filter, map, etc.)
		// should be called, and so on.

		//KStreamBuilder builder = ...;  // when using the Kafka Streams DSL
		//
		// OR
		//
		//TopologyBuilder builder = ...; // when using the Processor API
		
		TopologyBuilder builder = new TopologyBuilder();

		// add the source processor node that takes Kafka topic "source-topic" as input
		builder.addSource("Source", "source-topic")

		// add the WordCountProcessor node which takes the source processor as its upstream processor
		.addProcessor("Process", () -> new MyProcess1(), "Source")

		// create the countStore associated with the WordCountProcessor processor
		.addStateStore(countStore, "Process")

		// add the sink processor node that takes Kafka topic "sink-topic" as output
		// and the WordCountProcessor node as its upstream processor
		.addSink("Sink", "sink-topic", "Process");
		
		
		// Use the configuration to tell your application where the Kafka cluster is,
		// which serializers/deserializers to use by default, to specify security settings,
		// and so on.
		
		Properties settings = new Properties();
		// Set a few key parameters
		settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-first-streams-application");
		settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, hostIp + ":9092");
		settings.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		settings.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		
		// Customize the Kafka consumer settings
		settings.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 60000);

		// Customize a common client setting for both consumer and producer
		settings.put(CommonClientConfigs.RETRY_BACKOFF_MS_CONFIG, 100L);

		// Customize different values for consumer and producer
		settings.put(StreamsConfig.consumerPrefix(ConsumerConfig.RECEIVE_BUFFER_CONFIG), 1024 * 1024);
		settings.put(StreamsConfig.producerPrefix(ProducerConfig.RECEIVE_BUFFER_CONFIG), 64 * 1024);


		// Create an instance of StreamsConfig from the Properties instance
		StreamsConfig config = new StreamsConfig(settings);
		
		streams = new KafkaStreams(builder, config);
		
		// Start the Kafka Streams instance
		streams.start();

		streams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				// here you should examine the exception and perform an appropriate action!
				log.error(e.getMessage(), e);
			}
		});
		
	}
	
	
	//@Scheduled(cron = "*/1 * * * * *")
	public void process() {
		

	}
}
