package com.aact.infraservice.dto;

import com.aact.common.EmptyAsSupport;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprDTO {

    @JsonProperty("apprId")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "기안번호")
    private BigDecimal apprId;

    @JsonProperty("title")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "제목")
    private String title;

    @JsonProperty("reqDeptCode")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "요청부서")
    private String reqDeptCode;

    @JsonProperty("statusSid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "상태값")
    private BigDecimal statusSid;

    @JsonProperty("statusCode")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "상태코드")
    private String statusCode;

    @JsonProperty("statusReason")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "상태사유")
    private String statusReason;

    @JsonProperty("currentApprSid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "현재결재자값")
    private BigDecimal currentApprSid;

    @JsonProperty("writerSid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "작성자값")
    private BigDecimal writerSid;

    @JsonProperty("writerId")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "작성자")
    private String writerId;

    @JsonProperty("createDt")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "생성일")
    private String createDt;

    @JsonProperty("completeDt")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "완료일")
    private String completeDt;

    @JsonProperty("rejectReason")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려사유")
    private String rejectReason;

    @JsonProperty("rejectBySid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려처리자값")
    private BigDecimal rejectBySid;

    @JsonProperty("rejectById")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려처리자")
    private String rejectById;

    @JsonProperty("rejectDt")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려일")
    private LocalDateTime rejectDt;

    @JsonProperty("updateDt")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "수정일")
    private LocalDateTime updateDt;

    @JsonProperty("usableFlag")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "사용여부")
    private String usableFlag;

    @JsonProperty("viewByRefDeptYn")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "참조부서여부")
    private String viewByRefDeptYn;

    @JsonProperty("refDeptCodes")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "참조부서")
    private List<String> refDeptCodes;

}