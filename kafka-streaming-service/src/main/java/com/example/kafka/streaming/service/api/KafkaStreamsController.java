package com.example.kafka.streaming.service.api;

import com.example.kafka.streaming.service.model.KafkaStreamsResponseModel;
import com.example.kafka.streaming.service.runner.KafkaStreamsRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/")
public class KafkaStreamsController {

    private final KafkaStreamsRunner kafkaStreamsRunner;

    @GetMapping("get-word-count-by-word/{word}")
    public ResponseEntity<KafkaStreamsResponseModel> getWordCountByWord(@PathVariable @NotEmpty String word) {
        Long wordCount = kafkaStreamsRunner.getValueByKey(word);
        KafkaStreamsResponseModel kafkaStreamsResponseModel = KafkaStreamsResponseModel
                .builder()
                .word(word)
                .wordCount(wordCount)
                .build();
        return ResponseEntity.ok(kafkaStreamsResponseModel);
    }

}
