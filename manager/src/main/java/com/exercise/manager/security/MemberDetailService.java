package com.exercise.manager.security;

import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Autowired
    public MemberDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        return User.builder()
                .username(member.getId())           // username 으로 사용
                .password(member.getPassWord())     // 암호화된 비밀번호여야 함
                .roles("USER")                      // 권한 부여
                .build();
    }
}
