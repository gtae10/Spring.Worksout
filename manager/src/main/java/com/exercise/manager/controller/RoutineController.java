    package com.exercise.manager.controller;

    import com.exercise.manager.domain.Member;
    import com.exercise.manager.domain.Routine;
    import com.exercise.manager.domain.RoutineGroup;
    import com.exercise.manager.service.RoutineGroupService;
    import com.exercise.manager.service.RoutineService;
    import com.exercise.manager.service.RoutineSetService;
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
        private final RoutineSetService routineSetService;

        public RoutineController(RoutineService routineService,
                                 WorksOutService worksOutService,
                                 RoutineGroupService routineGroupService,
                                 RoutineSetService routineSetService) {
            this.routineService = routineService;
            this.worksOutService = worksOutService;
            this.routineGroupService = routineGroupService;
            this.routineSetService = routineSetService;
        }


        //운동 선택 페이지
        @GetMapping("/list")
        public String list(
                @RequestParam(defaultValue = "전체") String bodyPart,
                @RequestParam Long groupId,
                Model model,
                HttpSession session
        ) {
            Member loginMember = (Member) session.getAttribute("loginMember");
            if (loginMember == null) return "redirect:/login";

            model.addAttribute("worksout", worksOutService.findByPart(bodyPart));
            model.addAttribute("selectedPart", bodyPart);
            model.addAttribute("groupId", groupId);
            model.addAttribute("lastUsed", routineService.findLastUsedByWorksOut(loginMember));
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

            // 입력한 세트 수만큼 동일한 무게/반복으로 초기 세트를 만들어둠 (이후 상세화면에서 세트별로 다르게 조정 가능)
            int initialSets = Math.max(routine.getSets(), 1);
            for (int i = 0; i < initialSets; i++) {
                routine.addSet(routine.getWeight(), routine.getReps());
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
            if (loginMember == null) return "redirect:/login";

            RoutineGroup routineGroup = routineGroupService.findById(groupId);

            model.addAttribute("routines", routineService.findByRoutineGroup(groupId));
            model.addAttribute("groupId", groupId);
            model.addAttribute("groupTitle", routineGroup.getTitle());
            model.addAttribute("bodyParts", routineGroup.getDistinctParts());
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

        //운동 하나의 세트별 상세 화면
        @GetMapping("/{id}/detail")
        public String routineDetail(@PathVariable Long id, Model model, HttpSession session) {
            Member loginMember = (Member) session.getAttribute("loginMember");
            if (loginMember == null) return "redirect:/login";

            Routine routine = routineService.findById(id);
            model.addAttribute("routine", routine);
            model.addAttribute("sets", routineSetService.findByRoutine(id));
            return "routine/routineDetail";
        }

        //세트 추가
        @PostMapping("/{id}/set/add")
        public String addSet(@PathVariable Long id,
                             @RequestParam(required = false) Double weight,
                             @RequestParam(required = false) Integer reps) {
            routineSetService.addSet(id, weight, reps);
            return "redirect:/routine/" + id + "/detail";
        }

        //세트 완료 체크 토글
        @PostMapping("/set/toggle/{setId}")
        public String toggleSet(@PathVariable Long setId,
                                @RequestParam Long routineId) {
            routineSetService.toggleCompleted(setId);
            return "redirect:/routine/" + routineId + "/detail";
        }

        //세트 수정
        @PostMapping("/set/update/{setId}")
        public String updateSet(@PathVariable Long setId,
                                @RequestParam Long routineId,
                                @RequestParam double weight,
                                @RequestParam int reps) {
            routineSetService.updateSet(setId, weight, reps);
            return "redirect:/routine/" + routineId + "/detail";
        }

        //세트 삭제
        @PostMapping("/set/delete/{setId}")
        public String deleteSet(@PathVariable Long setId,
                                @RequestParam Long routineId) {
            routineSetService.deleteSet(setId);
            return "redirect:/routine/" + routineId + "/detail";
        }

    }
