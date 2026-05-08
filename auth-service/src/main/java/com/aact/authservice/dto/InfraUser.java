package com.aact.authservice.dto;

import com.aact.common.EmptyAsSupport;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

public class InfraUser {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SaveUserDTO(
            @JsonProperty("USER_ID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String userId,
            @JsonProperty("USER_ID_CHANGE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userIdChange,
            @JsonProperty("USER_PASSWORD") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String userPass,
            @JsonProperty("USER_PASSWORD_HP") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String userPassHp,
            @JsonProperty("USER_NAME1") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userName1,
            @JsonProperty("USER_NAME2") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String userName2,
            @JsonProperty("COMPANY_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String companyCode,
            @JsonProperty("BRANCH_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String branchCode,
            @JsonProperty("DEPARTMENT_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String deptCode,
            @JsonProperty("TERMINAL_CODE_WORK") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String terminalCode,
            @JsonProperty("TERMINAL_NAME_WORK") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String terminalName,
            @JsonProperty("DEFAULT_LANGUAGE_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("KOR") String langCode,
            @JsonProperty("EMAIL_ADDRESS") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String email,
            @JsonProperty("PHONE_NO") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String phone,
            @JsonProperty("MOBILE_NO") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String mobile,
            @JsonProperty("FAX_NO") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String fax,
            @JsonProperty("AUTH_WORKTIMELINE_YN") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("N") String workYn,
            @JsonProperty("AUTH_BOARD_WRITE_YN") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("N") String boardYn,
            @JsonProperty("AUTH_IN_CANCEL_YN") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("N") String inYn,
            @JsonProperty("AUTH_BOARDHP_WRITE_YN") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("N") String boardHpYn,
            @JsonProperty("AUTH_IT_BOARD_YN") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("N") String itYn) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UserRelDTO(
            @JsonProperty("USER_SID") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") BigDecimal userSid,
            @JsonProperty("CLASS_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String classCode,
            @JsonProperty("CODE_CODE") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("*") String codeCode,
            @JsonProperty("YYYY") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("0000") String yyyy,
            @JsonProperty("VALUE1") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value1,
            @JsonProperty("VALUE2") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value2,
            @JsonProperty("VALUE3") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value3,
            @JsonProperty("VALUE4") @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class) @EmptyAsSupport.EmptyAs("") String value4) {
    }

}
