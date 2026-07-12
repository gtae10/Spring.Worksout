package com.exercise.manager.controller;

import com.exercise.manager.domain.Member;
import com.exercise.manager.service.WorksOutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/worksout")
public class WorksOutController {

    private final WorksOutService worksOutService;

    public WorksOutController(WorksOutService worksOutService) {
        this.worksOutService = worksOutService;
    }

    // 운동 목록 (부위별 열람, 루틴에 넣지 않고 그냥 종목만 확인하는 용도)
    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "전체") String bodyPart,
                       Model model, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        model.addAttribute("worksout", worksOutService.findByPart(bodyPart));
        model.addAttribute("selectedPart", bodyPart);
        return "worksout/list";
    }
}
