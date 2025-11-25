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
                )
                .csrf(csrf -> csrf.disable()); // 개발 단계

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
