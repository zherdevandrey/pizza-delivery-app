package com.example.analytic.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.*;
import java.io.Serializable;

public interface KafkaConsumer<K extends Serializable, V extends SpecificRecordBase>{

    void receive(List<V> messages, List<Integer> keys, List<Integer> partitions, List<Long> offset);

}
