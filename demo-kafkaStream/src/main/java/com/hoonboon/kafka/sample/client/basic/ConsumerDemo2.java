package com.hoonboon.kafka.sample.client.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsumerDemo2 {

	private static final Logger log = LoggerFactory.getLogger(ConsumerDemo2.class);
	
	Properties props;
	
	public ConsumerDemo2() {

		props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "testGrp-01");
		props.put("enable.auto.commit", "false");
		//props.put("auto.commit.interval.ms", "1000");
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

			final int minBatchSize = 200;
			List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				
//				for (TopicPartition partition : records.partitions()) {
//
//					List<ConsumerRecord<String, String>> recordsInPartition = records.records(partition);
//					for (ConsumerRecord<String, String> record : recordsInPartition) {
//						log.info(
//								String.format(
//										"partition = %d, offset = %d, key = %s, value = %s",
//										record.partition(), record.offset(), record.key(), record.value()));
//					}
//
//					long lastOffset = recordsInPartition.get(recordsInPartition.size() - 1).offset();
//					consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
//				}
				
				
				for (ConsumerRecord<String, String> record : records) {
					buffer.add(record);
				}
				if (buffer.size() >= minBatchSize) {
					log.info(String.format("processing batch size = %d", buffer.size()));
					consumer.commitSync();
					buffer.clear();
				}
				
				log.info("going to take a nap ...");
				Thread.sleep(6000);
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
