package com.example.elastic.query.web.client.api;

import com.example.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.example.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import com.example.elastic.query.web.client.service.ElasticQueryWebClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QueryController {

    private final ElasticQueryWebClient client;

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel,
                              Model model) {
        log.info("Querying with text {}", requestModel.getText());
        List<ElasticQueryWebClientResponseModel> responseModels = client.getDataByText(requestModel);
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

}
