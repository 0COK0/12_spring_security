package com.ohgiraffers.jwtsecurity.auth.handler;

import com.ohgiraffers.jwtsecurity.auth.model.DetailsUser;
import com.ohgiraffers.jwtsecurity.auth.model.service.DetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {


    @Autowired
    private DetailsService detailsService; // 필드 주입을 받음

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // 매치스라는 메소드로 확인을 함 컴파일나면 빈으로 등록해주지않아서그럼

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken loginToken = (UsernamePasswordAuthenticationToken) authentication;

        String id = loginToken.getName(); // 토큰에서 겟네임해서 꺼내주고
        String pass = (String) loginToken.getCredentials(); // 스트링으로 다운캐스팅해줌

        DetailsUser detailsUser = (DetailsUser) detailsService.loadUserByUsername(id);
        // 여기에 아이디를 주고 디테일s유저를 반환해줌(부모를 반환)

        if(!passwordEncoder.matches(pass, detailsUser.getPassword())) { // 세션기반했을때
            throw new BadCredentialsException(pass + "는 틀린 비밀번호입니다.");
        }
        return new UsernamePasswordAuthenticationToken(detailsUser, pass, detailsUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) { // 언제프로바이더를 사용할지
        return authentication.equals(UsernamePasswordAuthenticationToken.class); // 토큰이 맞으면 반환하게끔 지정해줌
    }
}
