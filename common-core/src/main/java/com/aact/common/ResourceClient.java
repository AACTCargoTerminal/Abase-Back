package com.aact.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@AllArgsConstructor
public class ResourceClient {
    private ClientName name;
    private WebClient webClient;
    private String testPath;
    private Integer responseTimeout;
}
