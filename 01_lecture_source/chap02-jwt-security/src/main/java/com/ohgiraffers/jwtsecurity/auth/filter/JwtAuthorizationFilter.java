package com.ohgiraffers.jwtsecurity.auth.filter;

import com.ohgiraffers.jwtsecurity.auth.model.DetailsUser;
import com.ohgiraffers.jwtsecurity.common.AuthConstants;
import com.ohgiraffers.jwtsecurity.common.UserRole;
import com.ohgiraffers.jwtsecurity.common.utils.TokenUtils;
import com.ohgiraffers.jwtsecurity.user.dto.LoginUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        List<String> roleLessList = Arrays.asList("/signup");

        if (roleLessList.contains((request.getRequestURI()))) {
            chain.doFilter(request, response); // 다음내용이없으면 다음핸들러를 작동시켜주는 메소드
            return;
        }

        String header = request.getHeader(AuthConstants.AUTH_HEADER); // 상수로 유저롤을 저장??

        try {

            if (header != null && !header.equalsIgnoreCase("")) {
                String token = TokenUtils.splitHeader(header);

                if (TokenUtils.isValidToken(token)) {

                    Claims claims = TokenUtils.getClaimsFromToken(token);

                    // 유저정보
                    DetailsUser authentication = new DetailsUser();

                    LoginUserDTO user = new LoginUserDTO();
                    user.setUserName(claims.get("userName").toString());
                    user.setUserRole(UserRole.valueOf(claims.get("userRole").toString()));
                    authentication.setUser(user);

                    AbstractAuthenticationToken authenticationToken
                            = UsernamePasswordAuthenticationToken
                            .authenticated(authentication, token, authentication.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    chain.doFilter(request, response);


                } else {
                    throw new RemoteException("token이 유효하지 않습니다.");
                }

            } else {
                throw new RuntimeException("token이 존재하지 않습니다.");
            }

        } catch (Exception e) {


        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();

        JSONObject jsonObject = jsonResponseWrapper(e);
        printWriter.print(jsonObject);
        printWriter.flush();
        printWriter.close();
        }
    }

    private JSONObject jsonResponseWrapper(Exception e) {
        String resultMsg = "";

        if (e instanceof ExpiredJwtException) {
            resultMsg = "Token Expired";
        } else if (e instanceof SignatureException) {
            resultMsg = "Token SignatureException";
        } else if (e instanceof JwtException) {
            resultMsg = "Token Parsing JwtException";
        } else {
            resultMsg = "other Token error";
        }

        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("status", 401);
        jsonMap.put("message", resultMsg);
        jsonMap.put("reason", e.getMessage());

        return new JSONObject(jsonMap);

    }
}
