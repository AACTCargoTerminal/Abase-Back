package com.aact.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SetUserDTO {
	private String pass;
	private String passHp;
	private String userName2;
	private String langCode;
	private String email;
	private String phone;
	private String mobile;
	private String fax;
}
