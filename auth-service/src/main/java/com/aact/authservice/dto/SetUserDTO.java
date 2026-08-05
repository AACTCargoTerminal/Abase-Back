package com.aact.authservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
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