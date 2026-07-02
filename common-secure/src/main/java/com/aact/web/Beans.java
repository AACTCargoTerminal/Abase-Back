package com.aact.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableRedisHttpSession
public class Beans {
    private final Environment env;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/**").permitAll().anyRequest().authenticated())
                .formLogin(f -> f.disable()).httpBasic(b -> b.disable());
        return http.build();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        String domain = env.getProperty("session.cookie.domain");
        DefaultCookieSerializer s = new DefaultCookieSerializer();
        s.setCookieName("WMSSESSION");
        s.setUseHttpOnlyCookie(true);
        log.info("session.cookie.domain = [{}]", domain);
        if(domain != null && !domain.isBlank()) {
            s.setUseSecureCookie(true);
            s.setDomainName(domain);
        }else{
            s.setUseSecureCookie(false);
        }

        s.setSameSite("Lax");
        s.setUseBase64Encoding(true);

        s.setCookiePath("/");
        return s;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        String url = env.getProperty("cli.cors.url");
        if(url==null||url.isEmpty()){
            log.error("corsConfigurationSource : {}","CORS origin 설정이 없습니다.");
            throw new IllegalStateException("CORS origin 설정이 없습니다.");
        }
        List<String> pattern = Arrays.stream(url.split(";"))
                .map(String::trim)           // 공백 제거
                .filter(s -> !s.isEmpty())   // 빈값 제거
                .toList();
        if (pattern.isEmpty()) {
            log.error("corsConfigurationSource : {}","유효한 CORS origin이 없습니다.");
            throw new IllegalStateException("유효한 CORS origin이 없습니다.");
        }
        CorsConfiguration config = new CorsConfiguration();

        // ★ 반드시 "정확한" 오리진을 지정 (와일드카드 * 금지)
        config.setAllowedOriginPatterns(pattern);

        // 사용할 메서드/헤더 지정
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(
                List.of("Content-Type", "Authorization", "X-Requested-With", "Accept", "PGMID", "MENU-MODE"));

        // ★ credentials 허용 (Set-Cookie, 쿠키 전송)
        config.setAllowCredentials(true);
        // 프리플라이트 캐시 시간
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Spring Session에 저장된 객체를 그대로 읽으려면 우선 JDK serializer가 안전함
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

}
