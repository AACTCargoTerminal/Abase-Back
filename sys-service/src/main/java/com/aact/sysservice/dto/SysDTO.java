package com.aact.sysservice.dto;

import com.aact.common.EmptyAsSupport.EmptyAs;
import com.aact.common.EmptyAsSupport.EmptyAsDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

public class SysDTO {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CustomerDTO {
		@JsonProperty("customerName")
		public String customerName;
		@JsonProperty("registrationNo")
		public String registrationNo;
		@JsonProperty("customerFlag")
		public String customerFlag;
		@JsonProperty("vendorFlag")
		public String vendorFlag;
		@JsonProperty("carrierFlag")
		public String carrierFlag;
		@JsonProperty("agencyFlag")
		public String agencyFlag;
		@JsonProperty("customsFlag")
		public String customsFlag;
		@JsonProperty("iataFlag")
		public String iataFlag;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SitaEmailDTO(
			@JsonProperty("title") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String title,
			@JsonProperty("msg") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String msg,
			@JsonProperty("schSid") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") BigDecimal schSid,
			@JsonProperty("mawb") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String mawb,
			@JsonProperty("senderEmail") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String senderEmail,
			@JsonProperty("recvEmai") String[] recvEmai) {
	}

	@Data
	@RequiredArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CodeSaveDTO{
		@JsonProperty("ASIS_CODE") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String asisCode;
		@JsonProperty("CLASS_CODE") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String classCode;
		@JsonProperty("CODE_CODE") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String code;
		@JsonProperty("CODE_DESCRIPTION") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String codeDesc;
		@JsonProperty("CODE_NAME1") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String codeName1;
		@JsonProperty("CODE_NAME2") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String codeName2;
		@JsonProperty("ORDER_SEQ") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal seq;
		@JsonProperty("VALUE1_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value1Char;
		@JsonProperty("VALUE2_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value2Char;
		@JsonProperty("VALUE3_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value3Char;
		@JsonProperty("VALUE4_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value4Char;
		@JsonProperty("VALUE5_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value5Char;
		@JsonProperty("VALUE6_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value6Char;
		@JsonProperty("VALUE7_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value7Char;
		@JsonProperty("VALUE8_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value8Char;
		@JsonProperty("VALUE9_CHAR") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") private String value9Char;
		@JsonProperty("VALUE1_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value1Num;
		@JsonProperty("VALUE2_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value2Num;
		@JsonProperty("VALUE3_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value3Num;
		@JsonProperty("VALUE4_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value4Num;
		@JsonProperty("VALUE5_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value5Num;
		@JsonProperty("VALUE6_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value6Num;
		@JsonProperty("VALUE7_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value7Num;
		@JsonProperty("VALUE8_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value8Num;
		@JsonProperty("VALUE9_NUMBER") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("0") private BigDecimal value9Num;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record CodeDelDTO(
			@JsonProperty("CLASS_CODE") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String classCode,
			@JsonProperty("CODE_CODE") @JsonDeserialize(using = EmptyAsDeserializer.class) @EmptyAs("") String code) {
	}

}
