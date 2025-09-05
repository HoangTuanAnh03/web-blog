package com.huce.webblog.service.impl;

import com.huce.webblog.dto.request.FilterRequest;
import com.huce.webblog.dto.request.SaveVectorRequest;
import com.huce.webblog.dto.request.SearchRequest;
import com.huce.webblog.dto.response.FilterResponse;
import com.huce.webblog.service.IPythonService;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.HttpProtocol;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;


@Service
public class PythonServiceImpl implements IPythonService {
    @NonFinal
    @Value("${api.python.filter}") String urlPython;

    @NonFinal
    @Value("${api.python.search}") String urlSearchPython;

    @Override
    public FilterResponse filterContent(String content){
//		WebClient client = WebClient.create("http://localhost:1992/api/v1");
        WebClient client = WebClient.builder()
                .baseUrl(urlPython)
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
        WebClient client = WebClient.create(urlPython);
        return client.post()
                .uri("/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FilterRequest(content))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public Object saveVector(String content, String pid, String title) {
        HttpClient httpClient = HttpClient.create().protocol(HttpProtocol.HTTP11);

        WebClient client = WebClient.builder()
                .baseUrl(urlSearchPython) // ví dụ "http://localhost:8000"
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        // Gửi thuần Map -> JSON luôn có đúng key "pid","title","content"
        Map<String, Object> body = new HashMap<>();
        body.put("pid", pid);
        body.put("title", title);     // optional có thể để null
        body.put("content", content); // bắt buộc phải có

        return client.post()
                .uri("/posts/index")
                .bodyValue(body)
                .retrieve()
                // Log chi tiết nếu server trả 4xx/5xx (nhìn được vì sao 422)
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .map(msg -> new RuntimeException("Upstream " + resp.statusCode() + ": " + msg))
                )
                .bodyToMono(String.class)
                .block();
    }

    @Override
    public Object deleteVector(String pid) {
        HttpClient httpClient = HttpClient.create().protocol(HttpProtocol.HTTP11);
        WebClient client = WebClient.builder()
                .baseUrl(urlSearchPython) // ví dụ "http://localhost:8000"
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        String uri = String.format("/posts/%s", pid);

        return client.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    @Override
    public Object chat(SearchRequest searchRequest) {
        HttpClient httpClient = HttpClient.create().protocol(HttpProtocol.HTTP11);

        WebClient client = WebClient.builder()
                .baseUrl(urlSearchPython) // ví dụ "http://localhost:8000"
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        Map<String, Object> body = new HashMap<>();
        body.put("query", searchRequest.getQuery());
        body.put("conversation_id", searchRequest.getConversation_id());     // optional có thể để null
        body.put("user_id", searchRequest.getUser_id());

        return client.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(searchRequest)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }
}
