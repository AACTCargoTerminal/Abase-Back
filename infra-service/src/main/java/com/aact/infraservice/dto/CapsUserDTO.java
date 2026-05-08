package com.aact.infraservice.dto;

import lombok.Data;

//U.id , U.name, U.idno, B.e_date, B.e_time, B.e_mode
@Data
public class CapsUserDTO {
	private String name;
	private Integer id;
	private String idno;
	private String date;
	private String e_mode;
}
