package com.ohgiraffers.jwtsecurity.auth.config;


import com.ohgiraffers.jwtsecurity.auth.filter.CustomAuthenticationFilter;
import com.ohgiraffers.jwtsecurity.auth.filter.JwtAuthorizationFilter;
import com.ohgiraffers.jwtsecurity.auth.handler.CustomAuthFailureHandler;
import com.ohgiraffers.jwtsecurity.auth.handler.CustomAuthSuccessHandler;
import com.ohgiraffers.jwtsecurity.auth.handler.CustomAuthenticationProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 메소드 단위에서 구현할 수 있게끔 설정 해준것?
public class WebSecurityConfig {

    /* 정적 자원에 대한 인증된 사용자의 접근을 설정하는 메소드 */
    @Bean // 정적 자원에 걸리지 않도록 빈으로 등록해줌
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)  // 위조방지를 걸어줌 (disable을 해두면 보안성이 낮아짐 브라우저에서 세션을 관리하지않는데 그걸 논브라우저라고함)
                .addFilterBefore(jwtAuthorizationFilter(), BasicAuthenticationFilter.class) // 상속받아서 구현
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 핸들러만들어서 설정해줄거임
                .httpBasic(basic -> basic.disable()); // 지정해준 인증절차를

        return http.build();
    }

    /* 사용자 요청(request)시 수행하는 메소드 */
    private JwtAuthorizationFilter jwtAuthorizationFilter() {

    return new JwtAuthorizationFilter(authenticationManager());
    }

    /* Authentization의 인증 메소드를 제공하는 메니져(= Provider의 인터페이스)를 반환하는 메소드 */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(customAuthenticationProvider());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() { // 핸들러의 속함
        return new CustomAuthenticationProvider();
    }

    /* 비밀번호를 암호화하는 인코더를 반환하는 메소드 */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* 사용자 인증 요청을 가로채서 로그인 로직을 수행하는 필토를 반환하는 메소드 */
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthLoginSuccessHandler());
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthLoginFailureHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    /* 사용자 정보가 맞을 경우 (= 로그인 성공 시)수행하는 핸들러를 반환하는 메소드 */
    private CustomAuthSuccessHandler customAuthLoginSuccessHandler() {
    return new CustomAuthSuccessHandler();
    }

    /* 사용자 정보가 맞지 않는 경우 (= 로그인 성공 시)수행하는 핸들러를 반환하는 메소드 */
    private CustomAuthFailureHandler customAuthLoginFailureHandler() {
        return new CustomAuthFailureHandler();
    }
}


