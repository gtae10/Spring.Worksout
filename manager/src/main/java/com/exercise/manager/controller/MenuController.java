package com.exercise.manager.controller;

import com.exercise.manager.domain.Member;
import com.exercise.manager.service.RoutineService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MenuController {

    private final RoutineService routineService;

    public MenuController(RoutineService routineService) {
        this.routineService = routineService;
    }

    // 메뉴 허브 화면
    @GetMapping("/menu")
    public String menu(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";
        return "menu/menu";
    }

    // 1RM(1회 최대 중량) 계산기 - 5대 운동별로 분할, 계산은 화면에서 JS로 즉시 처리
    @GetMapping("/menu/1rm")
    public String oneRepMax(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("lifts", List.of("스쿼트", "벤치프레스", "데드리프트", "오버헤드프레스(밀리터리프레스)", "바벨로우"));
        return "menu/oneRepMax";
    }

    // 개인 기록(PR) 트래커
    @GetMapping("/menu/pr")
    public String personalRecords(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("prList", routineService.findPersonalRecords(loginMember));
        return "menu/pr";
    }

    // 일일 칼로리/매크로 계산기 - 계산은 화면에서 JS로 즉시 처리, 체중은 프로필값으로 미리 채움
    @GetMapping("/menu/calorie")
    public String calorieCalculator(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("member", loginMember);
        return "menu/calorie";
    }
}
