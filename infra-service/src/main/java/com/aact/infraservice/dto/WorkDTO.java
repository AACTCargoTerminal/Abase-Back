package com.aact.infraservice.dto;

import com.aact.common.EmptyAsSupport;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.List;

public class WorkDTO {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SaveDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("halfType") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String halfType,
            @JsonProperty("teamCode") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String teamCode,
            @JsonProperty("userArray") List<SaveUserDTO> userArray
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SaveUserDTO(
            @JsonProperty("USER_ID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String userId,
            @JsonProperty("CLOSE_FLAG") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("N") String closeFlag,
            @JsonProperty("dayArray") List<SaveDayDTO> dayArray
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SaveDayDTO(
            @JsonProperty("DAY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String day,
            @JsonProperty("DAY_STR") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String dayStr) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApproveRowDTO(
            @JsonProperty("userSid") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("dayArray") List<String> dayArray) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApproveDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("userArray") List<ApproveRowDTO> userArray) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HrSearchDTO(
            @JsonProperty("type") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String type,
            @JsonProperty("reqFlag") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String reqFlag,
            @JsonProperty("deptCode") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String deptCode,
            @JsonProperty("terminalCode") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String terminalCode,
            @JsonProperty("toDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String toDate,
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String date,
            @JsonProperty("fromDate") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String fromDate,
            @JsonProperty("userName") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userName) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HrSchSaveDTO(
            @JsonProperty("YEAR") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String year,
            @JsonProperty("MON") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String mon,
            @JsonProperty("DAY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String day,
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("USER_ID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String userId,
            @JsonProperty("schArray") List<HrSchSaveRowDTO> schArray){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HrSchSaveRowDTO(

            @JsonProperty("SEQ") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal seq,
            @JsonProperty("WORK_TYPE_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String workTypeCode,
            @JsonProperty("ADD_WORK_HOUR") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") BigDecimal addHour) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HrReqSaveDTO(
            @JsonProperty("reqFlag") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String reqFlag,
            @JsonProperty("reqArray") List<HrReqSaveRowDTO> reqArray) {

    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HrReqSaveRowDTO(

            @JsonProperty("YEAR") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String year,
            @JsonProperty("MON") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String mon,
            @JsonProperty("DAY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String day,
            @JsonProperty("SEQ") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal seq,
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("DETAIL_STATUS") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String detailStatus) {

    }



}
