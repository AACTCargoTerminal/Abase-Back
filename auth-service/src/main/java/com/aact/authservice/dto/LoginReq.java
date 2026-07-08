package com.aact.authservice.dto;

import com.aact.common.EmptyAsSupport.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

public class LoginReq {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LoginDTO(
            @JsonProperty("username") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("*") String username,
            @JsonProperty("password") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("*") String password,
            @JsonProperty("terminal") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String terminal,
            @JsonProperty("menuType") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("*") String menuType
          ) {
    }

}
