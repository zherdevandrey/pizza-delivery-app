package com.example.kafka.producer.service;

import org.apache.avro.specific.SpecificRecordBase;

import java.io.Serializable;

interface KafkaProducer <K extends Serializable, V extends SpecificRecordBase>{

     void produce(String topic, K key, V message);

}
