package com.hoonboon.kafka.sample.client.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

@Component
public class ConsumerDemo3 {

	private static final Logger log = LoggerFactory.getLogger(ConsumerDemo3.class);

	Properties props;

	Map<TopicPartition, OffsetAndMetadata> currentOffsets;
	KafkaConsumer<String, String> consumer;
	
	public ConsumerDemo3() {

		props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "testGrp-01");
		props.put("enable.auto.commit", "false");
		props.put("max.poll.records", "1000");
		//props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		currentOffsets = new HashMap<>();
		//currentOffsets = Collections.emptyMap();
		
	}

	// TODO: uncomment this to enable execution
	//@Scheduled(cron = "*/2 * * * * *")
	public void process() {

		log.info("process() start"); 
		
		final class HandleRebalance implements ConsumerRebalanceListener { 
		    public void onPartitionsAssigned(Collection<TopicPartition> partitions) { 
		    }

		    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		        consumer.commitSync(currentOffsets); 
		    }
		}
		
		try {
			consumer = new KafkaConsumer<>(props);
			
			consumer.subscribe(Arrays.asList("my-topic"), new HandleRebalance());

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

					// batch commit offset
					int count = 0;
					for (ConsumerRecord<String, String> record : buffer) {
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
						if (count % 50 == 0)
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
					log.info("continue waiting for more records to be processed ...");
				}

				log.info("going to take a nap ...");
				Thread.sleep(5000);
			}

		} catch (InterruptedException e) {
			// do nothing
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
