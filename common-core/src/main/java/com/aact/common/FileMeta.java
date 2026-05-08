package com.aact.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FileMeta implements Serializable {

    private String dir;
    private String originName;
    private String changeName;
    private String fullpath;
    private long size;
    private String mime;
    private String ext;

}
