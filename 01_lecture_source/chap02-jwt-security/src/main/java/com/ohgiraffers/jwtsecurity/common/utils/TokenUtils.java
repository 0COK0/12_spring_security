package com.ohgiraffers.jwtsecurity.common.utils;

import com.ohgiraffers.jwtsecurity.user.dto.LoginUserDTO;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/* 토큰을 관리하기 위한 utils 모음 클래스 */
@Component
public class TokenUtils {

    private static String jwtSecretKey;
    private static Long tokenValidateTime;

    @Value("${jwt.key}")
    public void setJwtSecretKey(String jwtSecretKey) {
        TokenUtils.jwtSecretKey = jwtSecretKey;
    }

    @Value("${jwt.time}")
    public void setTokenValidateTime(Long tokenValidateTime) {
        TokenUtils.tokenValidateTime = tokenValidateTime;
    }

    /* header의 token을 분리하는 메소드 */
    public static String splitHeader(String header) {
        if (!header.equals("")) {
            return header.split(" ")[1];
        } else {
            return null;
        }
    }

    /* 토큰이 유효한지 확인하는 메소드 */
    public static boolean isValidToken(String token) {
        // 토큰을 복호화를 시켜줘야함

        try {
            Claims claims = getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            return false;
        } catch (JwtException e) {
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;

        }
    }

    /* 토큰을 복호화하는 메소드 */
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(jwtSecretKey)) // 암호화방식을 지정해서
                .parseClaimsJws(token)
                .getBody(); // Claims로 반환시켜 복화시켜주는 메소드이다
    }

    /* 토큰을 생성하는 메소드 */
    public static String generateJwtToken(LoginUserDTO user) {
        Date expireTime = new Date(System.currentTimeMillis() + tokenValidateTime);

        JwtBuilder builder = Jwts.builder()
                .setHeader(createHeader())
                .setClaims(createClaim(user))
                .setSubject("ohgiraffers token : " + user.getUserCode())
                .signWith(SignatureAlgorithm.HS256, createSignature())
                .setExpiration(expireTime);

        return builder.compact(); // 컴백트라는걸 통해서 문자열형식으로 반환
    }

    /* 토큰의 Header를 설정하는 메소드 */
    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("type", "jwt");
        header.put("alg", "HS256");
        header.put("date", System.currentTimeMillis());

        return header;
    }

    /* 사용자 정보를 기반으로 claim을 생성하는 메소드 */
    private static Map<String, Object> createClaim(LoginUserDTO user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getUserId());
        claims.put("userName", user.getUserName());
        claims.put("userRole", user.getUserRole());

        return claims;
    }

    /*JWT 서명을 발급하는 메소드 */
    private static Key createSignature() {
        byte[] secretByte = DatatypeConverter.parseBase64Binary(jwtSecretKey);
        return new SecretKeySpec(secretByte, SignatureAlgorithm.HS256.getJcaName());
    }
}

