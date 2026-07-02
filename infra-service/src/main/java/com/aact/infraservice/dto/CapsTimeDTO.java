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
            @JsonProperty("userSid") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("reqStartDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqStartDate,
            @JsonProperty("seq") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal seq,
            @JsonProperty("addDay") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0") BigDecimal addDay,
            @JsonProperty("reqStartTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqStartTime,
            @JsonProperty("reqEndTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqEndTime,
            @JsonProperty("addHour") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0") BigDecimal addHour,
            @JsonProperty("nightHour") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0") BigDecimal nightHour,
            @JsonProperty("holiHour") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0") BigDecimal holiHour,
            @JsonProperty("holiAddHour") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0") BigDecimal holiAddHour,
            @JsonProperty("remark") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String remark,
            @JsonProperty("files") List<MultipartFile> files) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SearchGroupDTO(
            @JsonProperty("userId") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String userId,
            @JsonProperty("reqStartDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqStartDate,
            @JsonProperty("addDay") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0") BigDecimal addDay,
            @JsonProperty("reqStartTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqStartTime,
            @JsonProperty("reqEndTime") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqEndTime,
            @JsonProperty("remark") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String remark,
            @JsonProperty("files") List<MultipartFile> files) {
    }

    public record CapsRangeResult(String time, String orgTime) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeleteDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("SEQ") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal seq,
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("LOG_SEQ") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") BigDecimal logSeq) {

    }

}
