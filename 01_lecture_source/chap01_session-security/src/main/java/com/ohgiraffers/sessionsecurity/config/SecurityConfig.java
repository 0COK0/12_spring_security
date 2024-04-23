package com.ohgiraffers.sessionsecurity.config;


import com.ohgiraffers.sessionsecurity.auth.model.AuthDetails;
import com.ohgiraffers.sessionsecurity.common.UserRole;
import com.ohgiraffers.sessionsecurity.config.handler.AuthFailHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration  // 설정이 돌아갈때
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthFailHandler authFailHandler;

    /* 비밀번호 암호화에 사용할 객체 BCryptPasswordEncoder bean 등록 */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* 정적 리소스에 대한 요청은 제외하는 설정 */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 무시를 해줌
        return web -> web.ignoring()
                // 시큐리티를 걸지 않고 제외 시키겠다는 것
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        /* 요청에 대한 권한 체크 (인가 받아서 인가 설정을해줌)*/
        http.authorizeHttpRequests( auth -> {
            // 로그인 하지않아도 볼 수 있는 페이지 permitAll 모든걸 허용해주겠다는 메소드(권한이 없어도 볼 수 있음)
            auth.requestMatchers("/auth/login", "/user/signup", "/auth/fail", "/", "/main").permitAll();
            // 유저롤에 만들어둔 얘가 맞으면 어드민에 들어갈 수 있게 해주겠다.
            auth.requestMatchers("/admin/*").hasAnyAuthority(UserRole.ADMIN.getRole());
            auth.requestMatchers("/user/*").hasAnyAuthority(UserRole.USER.getRole());
            auth.anyRequest().authenticated();


        }).formLogin( login -> {
            // 로그인에 대한 요청 로그 페이지를 찾아줌
            login.loginPage("/auth/login");
            // 맞추면 로그인을 할 수 있게
            login.usernameParameter("user");
            login.passwordParameter("pass");
            // 성공하면 루트로 이동할 수 있게
            login.defaultSuccessUrl("/", true);
            // 실패하면 지정 경로로 이동할 수 있게
            login.failureHandler(authFailHandler);

        }).logout( logout -> {              // 특정 url를 작업할 수 있고
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"));
            // 요 로그아웃이 되었을떄 쿠키를 지우고 성공하면 이동하겠다.
            logout.deleteCookies("JSESSIONID");
            logout.invalidateHttpSession(true);
            logout.logoutSuccessUrl("/");

        }).sessionManagement( session -> {
            session.maximumSessions(1);
            // 세션이 만료되었을때 루트로 이동하겠다.
            session.invalidSessionUrl("/");

            // 요청을(악의적으로) 보낼때 csrf기본적으로 방어를 제공하는데 개발할 동안은 잠깐 풀어놓곘다 라는 메소드라서
            // 개발하고 있을 때는 밑에 메소드를 설정해줘야 한다.)
        }).csrf( csrf -> csrf.disable());

        return http.build();
    }
}