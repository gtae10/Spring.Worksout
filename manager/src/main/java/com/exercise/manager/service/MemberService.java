package com.exercise.manager.service;

import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository,  PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String join(Member member) {
        vailidateDuplicateMember(member);
        //비밀번호 암호화
        member.setPassWord(passwordEncoder.encode(member.getPassWord()));

        memberRepository.save(member);
        return member.getId();
    }
    //전체 회원 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    //id 조희
    public Optional<Member> findById(String memberId){
        return memberRepository.findById((memberId));
    }
   //이름 조회
    public Optional<Member> findByName(String memberName){
        return memberRepository.findByName(memberName);
    }

    private void vailidateDuplicateMember(Member member) {
        memberRepository.findById(member.getId())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

}
