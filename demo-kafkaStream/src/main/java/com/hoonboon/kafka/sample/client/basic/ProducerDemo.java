package com.hoonboon.kafka.sample.client.basic;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProducerDemo {

	private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class);
	
	Properties props;
	
	public ProducerDemo() {
		
		props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
	}
	
	// TODO: uncomment this to enable execution
	//@Scheduled(cron = "*/3 * * * * *")
	public void process() {
		
		log.info("process() start"); 
		
		Producer<String, String> producer = new KafkaProducer<>(props);
		
		for (int i = 0; i < 100; i++)
			producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i) + ": " + System.currentTimeMillis()));

		producer.close();
		
		log.info("process() end");
	}
	
	
}
