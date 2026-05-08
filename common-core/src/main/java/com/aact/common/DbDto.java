package com.aact.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbDto {
	
	
	
	private Map<Integer, List<Map<String, DbTypeDTO>>> result;
	private String errFlag;
	private String errCode;
	private String errMsg;
	
	private Map<String,Object> retObj;

}
