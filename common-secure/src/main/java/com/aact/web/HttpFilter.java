package com.aact.web;

import com.aact.common.ClsUserInfo;
import com.aact.common.UserContext;
import com.aact.common.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpFilter extends OncePerRequestFilter {
    private final Environment env;
    private final StringRedisTemplate stringRedisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, java.io.IOException {

        try {
            HttpSession session = req.getSession(false);
            if (session != null) {
                String sid = session.getId();
                String reasonKey = "sess:kill-reason:" + sid;
                String mark = stringRedisTemplate.opsForValue().get(reasonKey);

                if (mark != null) {
                    stringRedisTemplate.delete(reasonKey);
                    // 현재 세션 무효화
                    try {
                        session.invalidate();
                    } catch (IllegalStateException ignore) {
                    }

                    // API 응답: 401 (웹앱이면 여기서 로그인 페이지로 리다이렉트 해도 됨)
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("text/plain;charset=UTF-8");
                    PrintWriter out = res.getWriter();
                    out.write("로그인 한 유저가 있습니다.");
                    out.flush();
                    return;
                } else {
                    if (req.getHeader("PGMID") == null) {
                        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        res.setContentType("text/plain;charset=UTF-8");
                        PrintWriter out = res.getWriter();
                        out.write("프로그램 ID 필요");
                        out.flush();
                        return;
                    }

                    ClsUserInfo dto = (ClsUserInfo) session.getAttribute("USER_PROFILE");
                    if (dto != null) {
                        dto.setSesId(Util.getCookie(req.getCookies()));
                        dto.setPgmId(req.getHeader("PGMID"));
                        dto.setMenuMode(req.getHeader("MENU-MODE"));
                        dto.setUserIpAddress(dto.getUserTerminalCodeWork() + ":" + req.getRemoteAddr());
                        UserContext.set(dto);
                    } else {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("text/plain;charset=UTF-8");
                        PrintWriter out = res.getWriter();
                        out.write("유저 정보 필요");
                        out.flush();
                        return;
                    }
                    String userKey = "sess:user:" + dto.getUserSid().toString();

                    String redisSessionId = stringRedisTemplate.opsForValue().get(userKey);
                    String currentSessionId = session.getId();

                    if (redisSessionId == null || !redisSessionId.equals(currentSessionId)) {
                        session.invalidate();

                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("text/plain;charset=UTF-8");
                        res.getWriter().write("재 로그인 필요");
                        return;
                    }

                    long ttlSec = stringRedisTemplate.getExpire(userKey);

                    if (ttlSec == -2 || ttlSec == -1) {
                        session.invalidate();

                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("text/plain;charset=UTF-8");
                        res.getWriter().write("재 로그인 필요");
                        return;
                    }

                    if (ttlSec <= 300L) {
                        session.setMaxInactiveInterval(30 * 60);
                        stringRedisTemplate.expire(userKey, 30 * 60, TimeUnit.SECONDS);
                    }

                }
            } else if (!validURI(req.getRequestURI()) && !validIp(req.getRemoteAddr())) {
                log.info("접속 URI = [{}]", req.getRequestURI());
                log.info("접속 IP = [{}]", req.getRemoteAddr());
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentType("text/plain;charset=UTF-8");
                PrintWriter out = res.getWriter();
                out.write("허용된 URL이 아닙니다.");
                out.flush();

                return;
            }

            chain.doFilter(req, res);
        } finally {

            UserContext.clear();

        }

    }

    private boolean validIp(String ip) {
        boolean ret = false;
        try {
            String url = env.getProperty("cli.valid.ip");

            if (url == null || url.isBlank()) {
                return false;
            }

            return Arrays.stream(url.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .anyMatch(s -> s.equals(ip));

        } catch (Exception e) {
            // TODO: handle exception
            ret = false;
        }
        return ret;
    }

    private boolean validURI(String uri) {
        boolean ret = false;
        try {
            String url = env.getProperty("cli.valid.url");

            if (url == null || url.isBlank()) {
                return false;
            }

            return Arrays.stream(url.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .anyMatch(s -> s.equals(uri));

        } catch (Exception e) {
            // TODO: handle exception
            ret = false;
        }
        return ret;
    }

}
