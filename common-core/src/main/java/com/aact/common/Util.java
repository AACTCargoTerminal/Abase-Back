package com.aact.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;

import java.math.BigDecimal;
import java.util.UUID;

public final class Util {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Util() {
    }

    public static String getGUID() {
        return UUID.randomUUID().toString();
    }

    // 쿠기 가져오기
    public static String getCookie(Cookie[] cookie) {
        String ret = "";
        try {
            for (Cookie c : cookie) {
                if ("WMSSESSION".equals(c.getName())) {
                    ret = c.getValue();
                    break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            ret = "";
        }
        return ret;
    }

    public static BigDecimal getDecimal(Object str) {
        BigDecimal ret = null;
        try {

            ret = new BigDecimal(str.toString());

        } catch (Exception e) {
            // TODO: handle exception

            ret = new BigDecimal(-1);
        }
        return ret;
    }

    public static Integer getInteger(Object str) {
        Integer ret = 0;
        try {

            ret = Integer.parseInt(str.toString());

        } catch (Exception e) {
            // TODO: handle exception

            ret = 0;
        }
        return ret;
    }

    public static Double getDouble(Object str) {
        Double ret = 0.0;
        try {

            ret = Double.parseDouble(str.toString());

        } catch (Exception e) {
            // TODO: handle exception

            ret = 0.0;
        }
        return ret;
    }

    //HHmm->HH:mm
    public static String formatTime_HHmm(String value){

        if (value == null || value.isBlank()) {
            return "";
        }

        value = value.trim();

        if (value.length() <= 2) {
            value = String.format("%02d00", Integer.parseInt(value));
        }
        else if (value.length() == 3) {
            value = "0" + value;
        }

        return value.substring(0, 2) + ":" + value.substring(2, 4);
    }

    public static String getStrChk(Object str) {
        String ret = "";
        try {

            if (str == null || str.toString().isEmpty()) {
            } else {
                ret = str.toString();
            }

        } catch (Exception e) {
            // TODO: handle exception
            ret = "";
        }
        return ret;
    }

    public static String getStrChk(Object str, String defStr) {
        String ret = "";
        try {

            if (str == null || str.toString().isEmpty()) {
                ret = defStr;
            } else {
                ret = str.toString();
            }

        } catch (Exception e) {
            // TODO: handle exception
            ret = defStr;
        }
        return ret;
    }

    public static <T> T castIfMatch(Object data, TypeReference<T> typeRef) {
        try {
            if (data == null) {
                return null;
            }

            return MAPPER.convertValue(data, typeRef);
        } catch (Exception e) {
            ResponseDTO.setError2("castIfMatch", e);
            return null;
        }
    }
}
