    package com.exercise.manager.controller;

    import com.exercise.manager.domain.Member;
    import com.exercise.manager.domain.Routine;
    import com.exercise.manager.domain.RoutineGroup;
    import com.exercise.manager.service.RoutineGroupService;
    import com.exercise.manager.service.RoutineService;
    import com.exercise.manager.service.WorksOutService;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    @Controller
    @RequestMapping("/routine")
    public class RoutineController {

        private final RoutineService routineService;
        private final WorksOutService worksOutService;
        private final RoutineGroupService routineGroupService;

        public RoutineController(RoutineService routineService,
                                 WorksOutService worksOutService,
                                 RoutineGroupService routineGroupService) {
            this.routineService = routineService;
            this.worksOutService = worksOutService;
            this.routineGroupService = routineGroupService;
        }


        //운동 선택 페이지
        @GetMapping("/list")
        public String list(
                @RequestParam(defaultValue = "전체") String bodyPart,
                @RequestParam Long groupId,
                Model model
        ) {
            model.addAttribute("worksout", worksOutService.findByPart(bodyPart));
            model.addAttribute("selectedPart", bodyPart);
            model.addAttribute("groupId", groupId);
            if (groupId == null) {
                throw new IllegalArgumentException("groupId가 누락되었습니다.");
            }
            return "routine/routineList";
        }

        //루틴 추가
        @PostMapping("/new/{groupId}")
        public String createRoutine(@PathVariable Long groupId, @ModelAttribute Routine routine, HttpSession session) {
            Member loginMember = (Member) session.getAttribute("loginMember");
           RoutineGroup routineGroup = routineGroupService.findById(groupId);

            routine.setRoutineGroup(routineGroup);

            if (routine.getWorksOut() != null && routine.getWorksOut().getId() != null) {
                worksOutService.findById(routine.getWorksOut().getId())
                        .ifPresent(w -> {
                            routine.setWorkout(w.getWorkout()); //운동 이름 복사
                            routine.setPart(w.getPart());       //운동 부위 복사
                        });
            }
            routineService.saveRoutine(loginMember, routine);
            return "redirect:/routine/list?groupId=" + groupId;
        }

        //내 루틴그룹 보기
        @GetMapping("/my")
        public String myRoutine(HttpSession session, Model model) {
            Member loginMember = (Member) session.getAttribute("loginMember");
            model.addAttribute("groups", routineGroupService.findByMember(loginMember)); // ✅ 그룹 전달
            return "routine/routinegroupList_my";
        }
        //루틴 그룹안에 루틴 보기
        @GetMapping("/group/{groupId}")
        public String viewRoutineGroup(@PathVariable Long groupId, Model model, HttpSession session) {
            Member loginMember = (Member) session.getAttribute("loginMember");
            model.addAttribute("routines", routineService.findByRoutineGroup(groupId));
            model.addAttribute("groupId", groupId);
            if (loginMember == null) return "redirect:/";
            return "routine/worksoutList";
        }

        //루틴 삭제
        @PostMapping("/delete/{id}/{groupId}")
        public String deleteRoutine(@PathVariable Long id, @PathVariable Long groupId) {
            routineService.deleteById(id);
            return "redirect:/routine/group/" + groupId;
        }
        //수정
        @PostMapping("/update")
        public String updateRoutine(@RequestParam Long id,
                                    @RequestParam int sets,
                                    @RequestParam int reps,
                                    @RequestParam(required = false) Double weight,
                                    @RequestParam String date,
                                    @RequestParam Long groupId){
            routineService.updateRoutine(id,sets,reps,weight);
            return "redirect:/calender/detail?date="+ date;

        }

    }
