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
            @JsonProperty("dayArray") List<SaveDayDTO> dayArray
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SaveDayDTO(
            @JsonProperty("DAY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String day,
            @JsonProperty("DAY_STR") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String dayStr) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CloseDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("userArray") List<BigDecimal> userArray) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApproveDTO(
            @JsonProperty("date") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String date,
            @JsonProperty("day") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String day,
            @JsonProperty("userArray") List<BigDecimal> userArray) {
    }
}
