package com.ohgiraffers.jwtsecurity.auth.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgiraffers.jwtsecurity.user.dto.LoginUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);


    }

    /* 지정된 url 요청 시 해당 요청을 가로채서 검증 로직을 수행하는 메소드 */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //  요청정보를 가지고 있고 여기다가 담아줌
        UsernamePasswordAuthenticationToken authRequest;

        try {
            authRequest = getAuthRequest(request);
            setDetails(request, authRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    /* 사용자의 로그인 요청 시 요청 정보를 임시 토큰에 저장하는 메소드 (얘를 가지고 계속 검증을 하는 작업) */
    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) throws IOException {

        // 제이슨형식으로 변환시켜줌(JsonParser에 대한 설정 ) + 자동으로 닫아주는
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

        LoginUserDTO user = objectMapper.readValue(request.getInputStream(), LoginUserDTO.class);
        return new UsernamePasswordAuthenticationToken(user.getUserId(), user.getUserPass());
    }
}
