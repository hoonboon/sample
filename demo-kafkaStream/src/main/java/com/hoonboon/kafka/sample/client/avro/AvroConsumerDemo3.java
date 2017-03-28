package com.hoonboon.kafka.sample.client.avro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hoonboon.kafka.sample.domain.User;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

//TODO: uncomment this to enable execution
//@Component
public class AvroConsumerDemo3 {

	private static final Logger log = LoggerFactory.getLogger(AvroConsumerDemo3.class);
	
	static final String hostIp = "10.200.10.1";
	
	Properties props;
	
	KafkaConsumer<String, User> consumer;
	
	Map<TopicPartition, OffsetAndMetadata> currentOffsets;
	
	boolean isShutdown = false;
	
	public AvroConsumerDemo3() {

		props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hostIp + ":9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-testGrp-01");
//		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put("schema.registry.url", "http://" + hostIp + ":8081");
		
		currentOffsets = new HashMap<>();
		
		consumer = new KafkaConsumer<>(props);
		
		consumer.subscribe(
				Arrays.asList("my-topic-avro"),
				new ConsumerRebalanceListener() {
					
					@Override
					public void onPartitionsRevoked(Collection<TopicPartition> arg0) {
						consumer.commitSync(currentOffsets); 
					}
					
					@Override
					public void onPartitionsAssigned(Collection<TopicPartition> arg0) {
					}
					
				});

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
		
	}
	
	// TODO: uncomment this to enable execution
	//@Scheduled(cron = "*/1 * * * * *")
	public void process() {

		log.info("process() start"); 
		
		try {
			
			final int minBatchSize = 20;
			List<ConsumerRecord<String, User>> buffer = new ArrayList<>();
			
			while (!isShutdown) {
				ConsumerRecords<String, User> records = consumer.poll(100);
				
				for (ConsumerRecord<String, User> record : records) {
					buffer.add(record);
				}
				
				if (buffer.size() >= minBatchSize) {
					log.info(String.format("processing batch size = %d", buffer.size()));

					// batch commit offset
					int count = 0;
					for (ConsumerRecord<String, User> record : buffer) {
						log.info(
								String.format(
										"partition = %d, offset = %d, key = %s, value = %s",
										record.partition(), record.offset(), record.key(), record.value()));
						
						/**
						 * From: http://kafka.apache.org/0102/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
						 * Note: 
						 *   The committed offset should always be the offset of the next message that your application will read. 
						 *   Thus, when calling commitSync(offsets) you should add one to the offset of the last message processed.
						 * 
						 * TODO:
						 * - If data being processed is only committed at the same rate as the offset, then need to consider updating the currentOffSets in batch commit block.
						 *   - Reason: HandRebalance() will commit the last offset based on the currentOffSets values in the event of Consumer Rebalance.
						 */
						currentOffsets.put(
								new TopicPartition(record.topic(), record.partition()), 
								new OffsetAndMetadata(record.offset()+1)); 
						
						count++;
						if (count % 6 == 0)
						{
							log.info("batch committing count: {}", count);
							consumer.commitAsync(
									currentOffsets,
									new OffsetCommitCallback() {
										public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
											if (e != null)
												log.error("Commit failed for offsets {}", offsets, e);
										}
									});
						}
					}
					
					// commit any leftover from previous batch offset
					log.info("batch committing count: {}", count);
					consumer.commitAsync(
							currentOffsets,
							new OffsetCommitCallback() {
								public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
									if (e != null)
										log.error("Commit failed for offsets {}", offsets, e);
								}
							});

//					// commit last offset once at the end of current poll() 
//					consumer.commitAsync(
//							new OffsetCommitCallback() {
//								public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) {
//									if (e != null)
//										log.error("Commit failed for offsets {}", offsets, e);
//								}
//							}); 

					buffer.clear();
				}
				else {
					log.info("waiting for more records to be processed ...");
				}

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
			// attempt to retry any previous async commit failure before exiting
			try {
				consumer.commitSync(currentOffsets);
			} finally {
				consumer.close();
			}
			log.info("process() end");
		}
		
	}
	
	
}
