package com.aact.authservice.dto;

import com.aact.common.EmptyAsSupport;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetUserDTO {

	@JsonProperty("pass")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "*",label = "암호")
	private String pass;

	@JsonProperty("passHp")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "*",label = "자료실암호")
	private String passHp;

	@JsonProperty("userName2")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "*",label = "공용어")
	private String userName2;

	@JsonProperty("langCode")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "*",label = "언어")
	private String langCode;

	@JsonProperty("email")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "")
	private String email;

	@JsonProperty("phone")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "")
	private String phone;

	@JsonProperty("mobile")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "")
	private String mobile;

	@JsonProperty("fax")
	@JsonDeserialize(using = EmptyAsSupport.EmptyAsDeserializer.class)
	@EmptyAsSupport.EmptyAs(value = "")
	private String fax;
}