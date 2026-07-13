package com.exercise.manager.controller;

import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.MemberRepository;
import com.exercise.manager.service.CalenderService;
import com.exercise.manager.service.MemberService;
import com.exercise.manager.service.RoutineGroupService;
import com.exercise.manager.service.WeightLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RoutineGroupService routineGroupService;
    private final CalenderService calenderService;
    private final WeightLogService weightLogService;

    public MemberController(PasswordEncoder passwordEncoder,
                            MemberRepository memberRepository,
                            MemberService memberService,
                            RoutineGroupService routineGroupService,
                            CalenderService calenderService,
                            WeightLogService weightLogService) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.routineGroupService = routineGroupService;
        this.calenderService = calenderService;
        this.weightLogService = weightLogService;
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/member/login.html
    }

    @GetMapping("/member/main")
    public String main(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("groups", routineGroupService.findByMember(loginMember));

        // 오늘 날짜에 배정된 루틴이 있으면 홈 화면 "기록" 퀵액션에 배지로 표시
        model.addAttribute("todayRoutine", calenderService.findByMemberAndDate(loginMember, LocalDate.now()));
        return "member/main";
    }

    @GetMapping("/member/new")
    public String createMember() {
        return "member/CreateMemberForm";
    }

    @PostMapping("/member/new")
    public String create(Member member, RedirectAttributes redirectAttributes) {
        try {
            memberService.join(member);
            redirectAttributes.addFlashAttribute("joined", true);
            return "redirect:/login";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "duplicate");
            redirectAttributes.addFlashAttribute("prevId", member.getId());
            redirectAttributes.addFlashAttribute("prevName", member.getName());
            return "redirect:/member/new";
        }
    }

    // 체중 설정/수정 (칼로리 계산용) - 어느 화면에서든 호출 가능, 저장 후 원래 화면으로 돌아감
    @PostMapping("/member/weight")
    public String updateWeight(@RequestParam double weight,
                               @RequestParam(required = false) String returnTo,
                               HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        memberService.updateWeight(loginMember, weight);
        weightLogService.logWeight(loginMember, weight);
        session.setAttribute("loginMember", loginMember); // 세션에 캐시된 회원 정보도 갱신

        return "redirect:" + (returnTo != null && !returnTo.isBlank() ? returnTo : "/member/main");
    }

    // 회원 프로필 화면 (이름/체중/키 수정)
    @GetMapping("/member/profile")
    public String profile(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("member", loginMember);
        return "member/profile";
    }

    @PostMapping("/member/profile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam(required = false) Double weight,
                                @RequestParam(required = false) Double height,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        memberService.updateProfile(loginMember, name, weight, height);
        if (weight != null) weightLogService.logWeight(loginMember, weight);
        session.setAttribute("loginMember", loginMember);
        redirectAttributes.addFlashAttribute("saved", true);

        return "redirect:/member/profile";
    }

    // 회원 탈퇴
    @PostMapping("/member/delete")
    public String deleteAccount(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        memberService.deleteAccount(loginMember);
        session.invalidate();
        return "redirect:/login";
    }

    // 내 루틴 데이터 CSV로 내보내기 (백업/제출용)
    @GetMapping("/member/export")
    public ResponseEntity<byte[]> exportData(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, "/login").build();
        }

        String csv = memberService.exportRoutinesCsv(loginMember);
        // 엑셀에서 한글 안 깨지게 UTF-8 BOM을 앞에 붙임
        byte[] bytes = ("\uFEFF" + csv).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"routine_export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }


}
