package com.aact.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T> {
	private String errFlag;
	private String errMsg;
	private T data;

	public static ResponseDTO<Map<Integer, List<Map<String, Object>>>> from(DbDto dto) {

		Map<Integer, List<Map<String, Object>>> data = new LinkedHashMap<Integer, List<Map<String, Object>>>();

		if (dto.getResult() != null) {

			for (Map.Entry<Integer, List<Map<String, DbTypeDTO>>> rows : dto.getResult().entrySet()) {
				Integer key = rows.getKey();
				List<Map<String, DbTypeDTO>> value = rows.getValue();

				List<Map<String, Object>> tmp = new ArrayList<Map<String, Object>>();
				for (Map<String, DbTypeDTO> row : value) {
					Map<String, Object> m = new LinkedHashMap<>(); // 순서 유지
					for (Map.Entry<String, DbTypeDTO> e : row.entrySet()) {
						DbTypeDTO dtot = e.getValue();
						Object val = null; // 필요시 dto.getType()에 따라 가공

						if (dtot != null) {
							var v = dtot.getObj();
							if (v instanceof java.time.LocalDateTime ldt) {
								DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
								val = ldt.format(FMT);
							} else {
								if (v == null) {
									if (dtot.getType() == DbTypeDTO.Type.DECIMAL) {
										val = BigDecimal.ZERO;
									} else {
										val = "";
									}
								} else {
									val = dtot.getObj();
								}

							}
						} else {
							val = "";
						}
						m.put(e.getKey(), val);
					}
					tmp.add(m);
				}
				data.put(key, tmp);
			}

		}

		return ResponseDTO.<Map<Integer, List<Map<String, Object>>>>builder().errFlag(dto.getErrFlag())
				.errMsg(dto.getErrMsg()).data(data).build();
	}

	public static List<Map<String, Object>> getData(List<Map<String, DbTypeDTO>> dto) {
		List<Map<String, Object>> tmp = new ArrayList<Map<String, Object>>();
		for (Map<String, DbTypeDTO> row : dto) {
			Map<String, Object> m = new LinkedHashMap<>(); // 순서 유지
			for (Map.Entry<String, DbTypeDTO> e : row.entrySet()) {
				DbTypeDTO dtot = e.getValue();
				Object val = null; // 필요시 dto.getType()에 따라 가공

				if (dtot != null) {
					var v = dtot.getObj();
					if (v instanceof java.time.LocalDateTime ldt) {
						DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						val = ldt.format(FMT);
					} else {
						if (v == null) {
							if (dtot.getType() == DbTypeDTO.Type.DECIMAL) {
								val = BigDecimal.ZERO;
							} else {
								val = "";
							}
						} else {
							val = dtot.getObj();
						}

					}
				} else {
					val = "";
				}
				m.put(e.getKey(), val);
			}
			tmp.add(m);
		}
		return tmp;
	}

	public static ResponseDTO<String> setError(String type, Object msg) {

		log.error(type, msg);
		return ResponseDTO.<String>builder().errFlag("Y").errMsg(msg.toString()).build();

	}

	public static <T> ResponseDTO<T> setError2(String type, Object msg) {

		log.error(type, msg);
		return ResponseDTO.<T>builder().errFlag("Y").errMsg(msg.toString()).build();

	}

}
