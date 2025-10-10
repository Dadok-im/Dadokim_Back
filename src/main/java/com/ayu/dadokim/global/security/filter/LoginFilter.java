package com.ayu.dadokim.global.security.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class LoginFilter extends AbstractAuthenticationProcessingFilter {

   public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";

   public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

   // 1. loginFilter 는 POST 요청 중 /login 경로를 통해 들어온 요청만 가로챈다.
   private static final RequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults()
           .matcher(HttpMethod.POST, "/login");

   private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;

   private String passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;

   // LoginSuccessHandler 에 필요한 객체 등록
   private final AuthenticationSuccessHandler authenticationSuccessHandler;

   // POST /login 일때만 해당 자식 클래스가 동작하며, 실제 인증은 부모 클래스인 AbstractAuthenticationProcessingFilter 으로 전달 (부모 생성자에 요청 매처와 AuthenticationManager를 전달)
   public LoginFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler) {

       super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
       // 로그인 성공 후 실행할 authenticationSuccessHandler 등록, 실제 로직은 SecurityConfig 에서 진행
       this.authenticationSuccessHandler = authenticationSuccessHandler;
   }

   // 2. 상속받은 AbstractAuthenticationProcessingFilter 클래스의 attemptAuthentication 메서드 오버라이딩
   @Override
   public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
           throws AuthenticationException {
       // 1. 로그인 요청은 Post 요청만 허용한다. (GET, PUT 등의 요청은 예외를 발생시킨다.)
       if (!request.getMethod().equals("POST")) {
           throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
       }

       Map<String, String> loginMap;

       // 2. 요청 Body 에서 JSON 데이터를 추출한다. ()
       try {
           ObjectMapper objectMapper = new ObjectMapper();
           ServletInputStream inputStream = request.getInputStream();
           String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
           loginMap = objectMapper.readValue(messageBody, new TypeReference<>() {
           });
       } catch (IOException e) {
           throw new RuntimeException(e);
       }

       // 3. username 은 공백을 제거하고, null 이라면 빈 문자열으로 저장
       String username = loginMap.get(usernameParameter);
       username = (username != null) ? username.trim() : "";
       String password = loginMap.get(passwordParameter);
       password = (password != null) ? password : "";

       // 4. username, password 를 기반으로 인증토큰 생성 -> not JWT Token
       UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
       setDetails(request, authRequest);

       // 5. 최종적으로 AuthenticationManager 에게 인증토큰 값을 전달 후, AuthenticationManager return
       return this.getAuthenticationManager().authenticate(authRequest);
   }

   private void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
       authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
   }

   // LoginSuccessHandler 를 filterchain 에 등록
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }
}