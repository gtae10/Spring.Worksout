package com.exercise.manager.security;

import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final MemberRepository memberRepository;

    public SecurityConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/member/new", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")              // 커스텀 로그인 페이지 사용
                        .loginProcessingUrl("/login")     // POST 로그인 처리 URL
                        .usernameParameter("id")                 // 폼 name="id"
                        .passwordParameter("passWord")           // 폼 name="passWord"
                        .defaultSuccessUrl("/member/main", true)
                        .successHandler((request, response, authentication) -> {
                            String id = authentication.getName(); // 로그인한 ID
                            Member member = memberRepository.findById(id).orElse(null);
                            request.getSession().setAttribute("loginMember", member);
                            response.sendRedirect("/member/main");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                );
        // CSRF는 Spring Security 기본값(활성화)을 그대로 사용함.
        // Thymeleaf가 th:action 폼에는 CSRF 히든 필드를 자동으로 넣어주기 때문에
        // 이 프로젝트의 모든 폼(th:action 기준)은 별도 수정 없이 그대로 동작함.

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
