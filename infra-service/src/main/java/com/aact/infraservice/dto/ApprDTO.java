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

    @JsonProperty("drafterSid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "기안자값")
    private BigDecimal drafterSid;

    @JsonProperty("drafterId")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "기안자")
    private String drafterId;

    @JsonProperty("writerSid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "작성자값")
    private BigDecimal writerSid;

    @JsonProperty("writerId")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "작성자")
    private String writerId;

    @JsonProperty("draftTime")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "기안일")
    private String draftTime;

    @JsonProperty("statusChangeTime")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "상태변경일")
    private String statusChangeTime;

    @JsonProperty("completeTime")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "완료일")
    private String completeTime;

    @JsonProperty("rejectReason")
    private String rejectReason;

    @JsonProperty("rejectBySid")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려처리자값")
    private BigDecimal rejectBySid;

    @JsonProperty("rejectById")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려처리자")
    private String rejectById;

    @JsonProperty("rejectTime")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "반려일")
    private LocalDateTime rejectTime;

    @JsonProperty("updatedTime")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "수정일")
    private LocalDateTime updatedTime;

    @JsonProperty("usableFlag")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "사용여부")
    private String usableFlag;

    @JsonProperty("viewByRefDeptYn")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "참조부서여부")
    private String viewByRefDeptYn;

    @JsonProperty("refDeptCodes")
    private List<String> refDeptCodes;

    @JsonProperty("fromDate")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "시작일")
    private String fromDate;

    @JsonProperty("toDate")
    @JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
    @EmptyAsSupport.EmptyAs(value = "*", label = "종료일")
    private String toDate;

}