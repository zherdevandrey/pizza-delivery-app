package com.example.analytic.api;

import com.example.analytic.model.AnalyticsResponseModel;
import com.example.analytic.service.AnalyticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/")
public class AnalyticController {

    private final AnalyticService analyticService;

    @GetMapping
    public String test(){
        return "test";
    }


    @GetMapping("/get-word-count-by-word/{word}")
    public ResponseEntity<AnalyticsResponseModel> getAnalyticData(@PathVariable("word") String word){
        Optional<AnalyticsResponseModel> analyticData = analyticService.getAnalyticData(word);
        return analyticData
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(AnalyticsResponseModel.builder().build()));
    }

}
