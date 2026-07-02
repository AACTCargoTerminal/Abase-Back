package com.aact.commonClient.service;

import com.aact.common.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ClientService extends ServiceBase {

    private final Map<ClientName, ResourceClient> multiWebClient;

    public <T> ResponseDTO<T> get(ClientName clientName, Function<UriBuilder, URI> uriFunction, ParameterizedTypeReference<ResponseDTO<T>> typeRef){

        return execute(()->{
            ClsUserInfo info = UserContext.get();
            ResourceClient cli = multiWebClient.get(clientName);
            if(cli == null){
                throw new SysException("get",clientName+"서버 등록 안됨.");
            }
            testServer(cli,clientName);

            return cli.getWebClient()
                    .get()
                    .uri(uriFunction)
                    .header("PGMID",info.getPgmId()).cookie("WMSSESSION",info.getSesId())
                    .retrieve()
                    .bodyToMono(typeRef)
                    .block(Duration.ofMillis(cli.getResponseTimeout()));
        });

    }

    public <REQ,RES> ResponseDTO<RES> postJson(ClientName clientName,
                                               Function<UriBuilder, URI> uriFunction,
                                               REQ body,
                                               ParameterizedTypeReference<ResponseDTO<RES>> typeRef){
        return execute(() -> {

            ResourceClient cli = multiWebClient.get(clientName);

            if (cli == null) {
                throw new SysException("post", clientName + " 서버 등록 안됨.");
            }

            testServer(cli, clientName);
            ClsUserInfo info = UserContext.get();
            return cli.getWebClient()
                    .post()
                    .uri(uriFunction)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("PGMID",info.getPgmId()).cookie("WMSSESSION",info.getSesId())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(typeRef)
                    .block(Duration.ofMillis(cli.getResponseTimeout()));
        });
    }

    public <RES> ResponseDTO<RES> postMultipart(
            ClientName clientName,
            Function<UriBuilder, URI> uriFunction,
            MultipartBodyBuilder builder,
            ParameterizedTypeReference<ResponseDTO<RES>> typeRef
    ) {
        return execute(() -> {
            ResourceClient cli = multiWebClient.get(clientName);

            if (cli == null) {
                throw new SysException("post", clientName + " 서버 등록 안됨.");
            }

            testServer(cli, clientName);
            ClsUserInfo info = UserContext.get();
            return cli.getWebClient()
                    .post()
                    .uri(uriFunction)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .header("PGMID",info.getPgmId()).cookie("WMSSESSION",info.getSesId())
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(typeRef)
                    .block(Duration.ofMillis(cli.getResponseTimeout()));
        });
    }

    public void testServer(ResourceClient cli, ClientName clientName) {
        try {
            ClsUserInfo info = UserContext.get();
            cli.getWebClient()
                    .get()
                    .uri(cli.getTestPath())
                    .header("PGMID",info.getPgmId()).cookie("WMSSESSION",info.getSesId())
                    .retrieve()
                    .toBodilessEntity()
                    .block(Duration.ofMillis(cli.getResponseTimeout()));
        } catch (Exception e) {
            throw new SysException("get", clientName + "서버 테스트 실패", e);
        }
    }
}
