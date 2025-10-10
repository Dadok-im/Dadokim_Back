package com.ayu.dadokim.global.security.handler;

import com.ayu.dadokim.global.security.jwt.JwtUtil;
import com.ayu.dadokim.global.security.jwt.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class RefreshTokenLogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            // 1. 사용자 Logout Post 요청에서 Body 값을 받아온다.
            String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                    .lines().reduce("", String::concat);

            // 2. Body 값이 없다면 → 잘못된 요청
            if (!StringUtils.hasText(body)) {
                throw new IllegalArgumentException("로그아웃 요청에 Body가 없습니다.");
            }

            System.out.println("====================");
            System.out.println(body);
            System.out.println("====================");


            // 3. ObjectMapper 를 통해 읽어온 Body 값에 Refresh 토큰 값이 없다면 → 잘못된 요청
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);
            if (!jsonNode.has("refreshToken") || jsonNode.get("refreshToken").isNull()) {
                throw new IllegalArgumentException("로그아웃 요청에 refreshToken 값이 없습니다.");
            }
            String refreshToken = jsonNode.get("refreshToken").asText();

            System.out.println("====================");
            System.out.println(refreshToken);
            System.out.println("====================");

            // 4. refreshToken 값이 비어있다면 → 잘못된 요청
            if (!StringUtils.hasText(refreshToken)) {
                throw new IllegalArgumentException("refreshToken 값이 비어 있습니다.");
            }

            // 5. refresh 토큰 유효성 검증 실패 → 인증 오류
            Boolean isValid = jwtUtil.isValid(refreshToken, false);
            if (!isValid) {
                throw new SecurityException("유효하지 않은 refreshToken 입니다.");
            }

            // 6. Refresh 토큰 삭제 (DB/Redis 등)
            jwtService.removeRefresh(refreshToken);

        } catch (IOException e) {
            throw new RuntimeException("refreshTRoken 읽기 실패", e);
        }
    }
}

