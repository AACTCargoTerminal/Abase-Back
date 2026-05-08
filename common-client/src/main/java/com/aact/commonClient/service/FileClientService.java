package com.aact.commonClient.service;

import com.aact.common.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileClientService extends ServiceBase {
    private final ClientService clientService;
    private final Environment env;

    public ResponseDTO<?> fileSave(List<MultipartFile> files) {

        return execute(()->{
            String dir = env.getProperty("cli.fileServ.dir");
            if (files == null || files.isEmpty()) {
                throw new SysException("fileSave","업로드할 파일이 없습니다.");
            }
            if (dir == null || dir.isEmpty()) {
                throw new SysException("fileSave","서버에 생성할 고정 폴더가 없습니다.");
            }
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            for (MultipartFile file : files) {
                builder.part("files", file.getResource())
                        .filename(file.getOriginalFilename())
                        .contentType(MediaType.parseMediaType(
                                file.getContentType() != null
                                        ? file.getContentType()
                                        : MediaType.APPLICATION_OCTET_STREAM_VALUE
                        ));
            }
            ParameterizedTypeReference<ResponseDTO<List<FileMeta>>> typeRef =
                    new ParameterizedTypeReference<>() {
                    };
            return clientService.postMultipart(ClientName.FILE,
                    uriBuilder -> uriBuilder.path("/save").queryParam("dir", dir).build(),
                    builder,typeRef);
        });

    }
}
