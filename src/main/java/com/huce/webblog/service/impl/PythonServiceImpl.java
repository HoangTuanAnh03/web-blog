package com.huce.webblog.service.impl;

import com.huce.webblog.dto.request.FilterRequest;
import com.huce.webblog.dto.response.FilterResponse;
import com.huce.webblog.service.IPythonService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PythonServiceImpl implements IPythonService {
    @Override
    public FilterResponse filterContent(String content){
//		WebClient client = WebClient.create("http://localhost:1992/api/v1");
        WebClient client = WebClient.builder()
                .baseUrl("http://localhost:1992/api/v1")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(10 * 1024 * 1024))
                        .build())
                .build();
        return client.post()
                .uri("/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FilterRequest(content))
                .retrieve()
                .bodyToMono(FilterResponse.class)
                .block();
    }

    @Override
    public String summaryContent(String content) {
        WebClient client = WebClient.create("http://localhost:1992/api/v1");
        return client.post()
                .uri("/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FilterRequest(content))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
