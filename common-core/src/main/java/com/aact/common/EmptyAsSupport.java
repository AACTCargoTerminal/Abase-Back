package com.aact.common;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

public final class EmptyAsSupport {

    private EmptyAsSupport() {
    }

    /**
     * 필드에 붙여서 "빈값/null이면 이 값으로 대체"
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EmptyAs {
        String value(); // 문자열로 받고, 필드 타입에 맞게 변환
    }

    /**
     * 하나로 통합된 Deserializer - 필드 타입을 보고(String/BigDecimal/Boolean/Number 등) 적절히 변환 -
     * null/""/" " => @EmptyAs 값으로 치환
     */
    public static class EmptyAsDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

        private JavaType targetType; // 현재 필드 타입
        private String replacementText; // 어노테이션 값

        public EmptyAsDeserializer() {
        }

        private EmptyAsDeserializer(JavaType targetType, String replacementText) {
            this.targetType = targetType;
            this.replacementText = replacementText;
        }

        @Override
        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
                throws JsonMappingException {

            if (property == null)
                return this;

            EmptyAs ann = property.getAnnotation(EmptyAs.class);
            if (ann == null)
                ann = property.getContextAnnotation(EmptyAs.class);

            // @EmptyAs 없으면 그냥 기본 동작(= 아무 치환 안 함)
            if (ann == null)
                return this;

            return new EmptyAsDeserializer(property.getType(), ann.value());
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {


            String raw = p.getValueAsString(); // 숫자/불리언도 문자열로 들어올 수 있음
            boolean isEmpty = (raw == null || raw.trim().isEmpty());

            if ("*".equals(replacementText) && isEmpty) {
                throw new IOException("해당값은 필수 값입니다.");
            }

            String toParse = isEmpty ? replacementText : raw.trim();

            Class<?> rawClass = targetType.getRawClass();

            // 1) String
            if (rawClass == String.class) {
                if (isEmpty) {
                    if ("GUID".equals(toParse)) {
                        return Util.getGUID();
                    }
                }
                return toParse;
            }

            // 3) BigDecimal
            if (rawClass == BigDecimal.class) {
                return parseBigDecimal(toParse, ctxt, rawClass);
            }
            

            return toParse;
        }

        private BigDecimal parseBigDecimal(String s, DeserializationContext ctxt, Class<?> target) {
            try {
                return new BigDecimal(s);
            } catch (Exception e) {
                throw new BizException("INVALID_PARAM", "BigDecimal 변환 실패. value=" + s, e);
            }
        }
    }
}