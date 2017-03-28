package com.hoonboon.kafka.sample.stream.processorapi;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StreamProducerDemo {

	private static final Logger log = LoggerFactory.getLogger(StreamProducerDemo.class);
	
	static final String hostIp = "10.200.10.1";
	
	Properties props;
	
	Producer<String, String> producer;
	
	final String[] randomText01 = new String[] {"text01-1", "text01-2", "text01-3", "text01-4", "", "text01-5", "", "text01-6"};
	final String[] randomText02 = new String[] {"text02-1", "text02-2", "", "text02-3", "text02-4", "text02-5", "", "text02-6", "text02-7",};
	final String[] randomText03 = new String[] {"", "text03-1", "text03-2", "text03-3", "text03-4", "text03-5"};
	
	public StreamProducerDemo() {
		
		props = new Properties();
		props.put("bootstrap.servers", hostIp + ":9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		producer = new KafkaProducer<>(props);
		
		final Thread mainThread = Thread.currentThread();
		
		// Registering a shutdown hook so we can exit cleanly
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Starting exit...");
				// Note that shutdownhook runs in a separate thread
				
				// Stop the Kafka Streams instance
				producer.close();
				
				try {
					mainThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Scheduled(cron = "*/3 * * * * *")
	public void process() {
		
		log.info("process() start"); 
		
		String tempStr = null;
		for (int i = 0; i < 10; i++) {
			
			tempStr = 
					new StringBuffer()
					.append(randomText01[getRandomNumberInRange(0, randomText01.length-1)])
					.append(" " + randomText02[getRandomNumberInRange(0, randomText02.length-1)])
					.append(" " + randomText03[getRandomNumberInRange(0, randomText03.length-1)])
					.toString();
			
			producer.send(new ProducerRecord<String, String>("source-topic", "T:" + String.valueOf(System.currentTimeMillis()), tempStr));
		}
		
		log.info("process() end");
	}
	
	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

}
