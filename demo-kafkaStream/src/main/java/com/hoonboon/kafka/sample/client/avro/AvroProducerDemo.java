package com.hoonboon.kafka.sample.client.avro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hoonboon.kafka.sample.domain.User;

//TODO: uncomment this to enable execution
//@Component
public class AvroProducerDemo {

	private static final Logger log = LoggerFactory.getLogger(AvroProducerDemo.class);

	private static final String hostIp = "10.200.10.1"; 
	
	Properties props;
	
	KafkaProducer<String, GenericRecord> producer;
	
	boolean isShutdown = false;
	
	public AvroProducerDemo() {
		
		props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostIp + ":9092");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "avro-producer-demo");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // to enable strong ordering within partition
		props.put(ProducerConfig.RETRIES_CONFIG, 5);
		props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 200);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
		props.put("schema.registry.url", "http://" + hostIp + ":8081");
		
		producer = new KafkaProducer<>(props);

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
	//@Scheduled(cron = "*/3 * * * * *")
	public void process() {

		log.info("process() start"); 

		LocalDate dt = LocalDate.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

		String username = null;
		User newUserObj = null;

		try {

			while (!isShutdown) {

				for (int i = 0; i < 9; i++) {

					username = "user-" + i;

					if (i % 2 == 0)
						newUserObj = 
						new User(
								username, dt.minusYears(i).toString(), 
								LocalDateTime.now().format(dtf));
					else
						newUserObj = new User(username, null, null);

					producer.send(
							new ProducerRecord<String, GenericRecord>(
									"my-topic-avro", newUserObj.getUsername(), User.toGenericRecord(newUserObj)),
							new Callback() {

								@Override
								public void onCompletion(RecordMetadata rm, Exception ex) {
									if (ex != null)
										log.error("Send failure - RecordMetadata: " + rm.toString() + ", Error: " + ex.getMessage(), ex);
									else
										log.info("Send success - RecordMetadata: " + rm.toString());
								}
							});

				} // end for
				
				log.info("going to take a nap ...");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// do nothing
				}
				
			} // end while
			
		} catch (Exception e) {
			log.error("process(): " + e.getMessage(), e);
		} finally {
			producer.close();
		}

		log.info("process() end");
	}
	
	
}
