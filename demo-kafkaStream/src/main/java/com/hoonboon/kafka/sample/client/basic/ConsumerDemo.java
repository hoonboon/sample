package com.hoonboon.kafka.sample.client.basic;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ConsumerDemo {

	private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);
	
	Properties props;
	
	public ConsumerDemo() {

		props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "testGrp-01");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
	}
	
	// TODO: uncomment this to enable execution
	//@Scheduled(cron = "*/1 * * * * *")
	public void process() {

		log.info("process() start"); 

		KafkaConsumer<String, String> consumer = null;
		
		try {
			consumer = new KafkaConsumer<>(props);
			consumer.subscribe(Arrays.asList("my-topic"));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records)
					log.info(
							String.format(
									"partition = %d, offset = %d, key = %s, value = %s",
									record.partition(), record.offset(), record.key(), record.value()));

				log.info("going to take a nap ...");
				Thread.sleep(3000);
			}
		} catch (InterruptedException e) {
			// do nothing
		} catch (Exception e) {
			log.error("process(): " + e.getMessage(), e);
		}
		finally {
			consumer.close();
			log.info("process() end");
		}
		
	}
	
	
}
