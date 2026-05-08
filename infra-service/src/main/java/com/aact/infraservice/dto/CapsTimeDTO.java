package com.aact.infraservice.dto;

import com.aact.common.EmptyAsSupport;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public class CapsTimeDTO {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SearchDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("seq") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal seq,
            @JsonProperty("workType") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String workType,
            @JsonProperty("workTypeName") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String workTypeName,
            @JsonProperty("detailStartDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String detailStartDate,
            @JsonProperty("detailStartTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String detailStartTime,
            @JsonProperty("detailEndDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String detailEndDate,
            @JsonProperty("detailEndTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String detailEndTime,
            @JsonProperty("capsOrgStartTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String capsOrgStartTime,
            @JsonProperty("capsOrgEndTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String capsOrgEndTime,
            @JsonProperty("capsStartDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String capsStartDate,
            @JsonProperty("capsStartTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String capsStartTime,
            @JsonProperty("capsEndDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String capsEndDate,
            @JsonProperty("capsEndTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String capsEndTime,
            @JsonProperty("remark") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String remark,
            @JsonProperty("files") List<MultipartFile> files) {
    }

    public record CapsRangeResult(String capsStartDate, String capsStartTime, String capsEndDate, String capsEndTime,
                                  String capsOrgStartTime, String capsOrgEndTime) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeleteDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("SEQ") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal seq,
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid) {

    }

}
