package com.aact.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbTypeDTO {
	
	public enum Type{ VARCHAR,CLOB,DECIMAL,DATE,NULL,CURSOR,BLOB }
	public enum Inout{IN,OUT,INOUT, NULL}
	
	private Type type;
	private Inout inout;
	private String paramName;
	private Object obj;

}
