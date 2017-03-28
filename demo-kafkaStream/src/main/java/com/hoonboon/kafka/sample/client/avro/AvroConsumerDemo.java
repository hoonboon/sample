package com.hoonboon.kafka.sample.client.avro;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hoonboon.kafka.sample.domain.User;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

@Component
public class AvroConsumerDemo {

	private static final Logger log = LoggerFactory.getLogger(AvroConsumerDemo.class);
	
	static final String hostIp = "10.200.10.1";
	
	Properties props;
	
	KafkaConsumer<String, User> consumer;
	
	boolean isShutdown = false;
	
	public AvroConsumerDemo() {

		props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hostIp + ":9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-testGrp-01");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put("schema.registry.url", "http://" + hostIp + ":8081");
		
		consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("my-topic-avro"));
		
	}
	
	// TODO: uncomment this to enable execution
	//@Scheduled(cron = "*/1 * * * * *")
	public void process() {

		log.info("process() start"); 

		final Thread mainThread = Thread.currentThread();

		// Registering a shutdown hook so we can exit cleanly
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Starting exit...");
				// Note that shutdownhook runs in a separate thread
				isShutdown = true;

				try {
					mainThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		try {
			
			while (!isShutdown) {
				ConsumerRecords<String, User> records = consumer.poll(100);
				for (ConsumerRecord<String, User> record : records)
					log.info(
							String.format(
									"partition = %d, offset = %d, key = %s, value = %s",
									record.partition(), record.offset(), record.key(), record.value()));

				log.info("going to take a nap ...");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		} catch (Exception e) {
			log.error("process(): " + e.getMessage(), e);
		}
		finally {
			consumer.close();
			log.info("process() end");
		}
		
	}
	
	
}
