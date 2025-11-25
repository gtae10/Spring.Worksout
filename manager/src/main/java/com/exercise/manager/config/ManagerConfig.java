package com.exercise.manager.config;

import com.exercise.manager.repository.MemberRepository;
import com.exercise.manager.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ManagerConfig {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ManagerConfig(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public MemberService memberService(){
        return new MemberService(memberRepository, passwordEncoder);
    }

}
