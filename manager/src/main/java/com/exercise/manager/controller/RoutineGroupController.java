package com.exercise.manager.controller;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineGroup;
import com.exercise.manager.domain.WorksOut;
import com.exercise.manager.service.RoutineGroupService;
import com.exercise.manager.service.RoutineService;
import com.exercise.manager.service.WorksOutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/routinegroup")
public class RoutineGroupController {

    private final RoutineGroupService routineGroupService;
    private final WorksOutService worksOutService;
    private final RoutineService routineService;
    public RoutineGroupController(RoutineGroupService routineGroupService,
                                  WorksOutService worksOutService,
                                  RoutineService routineService) {
        this.routineGroupService = routineGroupService;
        this.worksOutService = worksOutService;
        this.routineService = routineService;
    }

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        Member loginMember = (Member)session.getAttribute("loginMember");
        if(loginMember == null) return "redirect:/";

        model.addAttribute("groups", routineGroupService.findByMember(loginMember));
        return "routinegroup/routinegroupList"; // ✅ 폴더/파일 경로
    }

    @GetMapping("/new")
    public String newRoutineGroup() {
        return "routinegroup/routinegroupForm";
    }
    @PostMapping("/new/{groupId}")
    public String createRoutine(
            @PathVariable Long groupId,
            @ModelAttribute Routine routine,
            HttpSession session
    ) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        RoutineGroup routineGroup = routineGroupService.findById(groupId);

        routine.setRoutineGroup(routineGroup);

        if (routine.getWorksOut() != null && routine.getWorksOut().getId() != null) {
            worksOutService.findById(routine.getWorksOut().getId())
                    .ifPresent(w -> {
                        routine.setWorkout(w.getWorkout());
                        routine.setPart(w.getPart());
                    });
        }

        routineService.saveRoutine(loginMember, routine);

        return "redirect:/routine/list?groupId=" + groupId;
    }
    @PostMapping("/new")
    public String create(@RequestParam String title, HttpSession session) {
        Member loginMember = (Member)session.getAttribute("loginMember");
        if(loginMember == null) return "redirect:/";

        RoutineGroup routineGroup = new RoutineGroup();
        routineGroup.setTitle(title);
        routineGroupService.save(loginMember, routineGroup);

        return "redirect:/routinegroup/list";
    }

        @PostMapping("/delete/{id}")
        public String delete(@PathVariable Long id) {
            routineGroupService.delete(id);
            return "redirect:/routinegroup/list";
        }
    }
