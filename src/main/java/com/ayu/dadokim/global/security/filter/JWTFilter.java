package com.ayu.dadokim.global.security.filter;


import com.ayu.dadokim.global.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. request Header 에서 Authorization 이 존재하는지 여부
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            // 1-1. 다음 filter 로 넘긴다. -> 최종적으로 springsecurity 필터가 로그인이 안되어 있으면 접근 거부를 할 수 있다.
            filterChain.doFilter(request, response);
            return;
        }

        // 2. jwt 가 존재하지만 접두사 Bearer 가 존재하지 않을 경우 -> 유효하지 않은 jwt token
        if (!authorization.startsWith("Bearer ")) {
            throw new ServletException("Invalid JWT token");
        }

        // 3. 토큰 파싱 : 접두사 Bearer 기준 띄어쓰기 한칸
        String accessToken = authorization.split(" ")[1];

        // 4. 유효한 JWT 토큰일 경우
        if (jwtUtil.isValid(accessToken, true)) {

            // 4-1. username, role 값을 파싱
            String username = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);

            // 4-2. role 값을 통해 권한 객체(new SimpleGrantedAuthority(role))를 List 형태로 저장
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // 4-3. 해당 username 과 role 값으로 SecurityContextHolder 로 생성
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } else {
            // 404 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰 만료 또는 유효하지 않은 토큰\"}");
            return;
        }

    }

}
