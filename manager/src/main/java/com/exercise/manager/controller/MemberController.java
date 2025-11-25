package com.exercise.manager.controller;

import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.MemberRepository;
import com.exercise.manager.service.MemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public MemberController(PasswordEncoder passwordEncoder,
                            MemberRepository memberRepository,
                            MemberService memberService) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/member/login.html
    }

    @GetMapping("/member/main")
    public String main() {
        return "member/main";
    }

    @GetMapping("/member/new")
    public String createMember() {
        return "member/CreateMemberForm";
    }

    @PostMapping("/member/new")
    @ResponseBody
    public String create(Member member) {
        try {
            memberService.join(member);
            return "<script>alert('회원가입이 완료되었습니다!');"
                    + "location.href='/login';</script>";
        } catch (IllegalStateException e) {
            return "<script>alert('이미 존재하는 ID입니다.');"
                    + "history.back();</script>";
        }
    }


}
