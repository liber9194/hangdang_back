package com.example.hangdang.security;

import com.example.hangdang.dto.LoginRequestDto;
import com.example.hangdang.entity.UserRoleEnum;
import com.example.hangdang.global.dto.ApiResponse;
import com.example.hangdang.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Api;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")//로그인 및 JWT 생성
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/login");
    }

    @Override//로그인 시도시 실행되는 함수
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            System.out.println("오류발생?!?");
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override//로그인 성공시 JWT 토큰 생성
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException{
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        String nickname = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getNickname();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        String token = jwtUtil.createToken(username, nickname, role);
        jwtUtil.addJwtToCookie(token, response);

//        jwtUtil.addJwtToHeader(JwtUtil.AUTHORIZATION_HEADER, token, response);
//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        // JSON으로 변환하여 응답
        ObjectMapper objectMapper = new ObjectMapper();
        //String jsonResponse = objectMapper.writeValueAsString(ApiResponse.successMessage(nickname + "님 환영합니다."));
        String jsonResponse = objectMapper.writeValueAsString(ApiResponse.successMessage(nickname + "님 환영합니다.", nickname));
        //String jsonData = objectMapper.writeValueAsString(ApiResponse.successData(nickname));

        response.setContentType("application/json");//응답 형식 지정
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException{
        // JSON으로 변환하여 응답
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(ApiResponse.error("로그인 실패"));

        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }

}