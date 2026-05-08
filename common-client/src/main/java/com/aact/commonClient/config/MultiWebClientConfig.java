package com.aact.commonClient.config;

import com.aact.common.ResourceClient;
import com.aact.common.Util;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MultiWebClientConfig {
    private final Environment env;

    @Bean("multiWebClient")
    public Map<com.aact.common.ClientName, ResourceClient> multiWebClient(){
        Map<com.aact.common.ClientName, ResourceClient> map = new LinkedHashMap<>();
        addIfPresent(map, com.aact.common.ClientName.AUTH,"client.auth");
        addIfPresent(map, com.aact.common.ClientName.FILE,"client.file");
        addIfPresent(map, com.aact.common.ClientName.INFRA,"client.infra");
        addIfPresent(map, com.aact.common.ClientName.SYS,"client.sys");
        addIfPresent(map, com.aact.common.ClientName.SAMS,"client.sams");
        addIfPresent(map, com.aact.common.ClientName.WAS,"client.was");
        log.info("등록된 WebClient: {}", map.keySet());
        return map;
    }

    private void addIfPresent(
            Map<com.aact.common.ClientName, ResourceClient> map,
            com.aact.common.ClientName name,
            String prefix
            ){
        String url = env.getProperty(prefix+".url");
        if (url == null || url.isBlank()) {
            log.info("{} Client 미사용: {}.url 없음", name, prefix);
            return;
        }
        Integer acceptTimeout = Util.getInteger(env.getProperty(prefix+".accept.timeout"));
        Integer resTimeout = Util.getInteger(env.getProperty(prefix+".response.timeout"));
        String testPath = Util.getStrChk(env.getProperty(prefix+".test.path"),"/");

        if(acceptTimeout == 0||resTimeout == 0||testPath.isBlank()){
            log.error(name + " WebClient 설정 부족");
            throw new IllegalStateException(name + " WebClient 설정 부족");
        }
        HttpClient client = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS,acceptTimeout)
                .responseTimeout(Duration.ofMillis(acceptTimeout));

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();

        try {
            webClient.get()
                    .uri(testPath)
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofMillis(resTimeout));

            log.info("{} Client 연결 성공: {}{}", name, url, testPath);

        } catch (Exception e) {
            log.error("{} Client 연결 실패: {}{}", name, url, testPath, e);
            throw new IllegalStateException(name + " 서버 연결 실패", e);
        }

        ResourceClient cli = new ResourceClient(
                name,
                webClient,
                testPath,
                resTimeout
        );

        map.put(name, cli);

    }


}
