package com.exercise.manager.controller;

import com.exercise.manager.domain.*;
import com.exercise.manager.service.CalenderService;
import com.exercise.manager.service.RoutineGroupService;
import com.exercise.manager.service.RoutineService;
import com.exercise.manager.service.WorkOutRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calender")
public class CalenderController {

    private final CalenderService calendarService;
    private final WorkOutRecordService workOutRecordService;
    private final RoutineGroupService routineGroupService;
    private final RoutineService routineService;
    public CalenderController(CalenderService calendarService,
                              WorkOutRecordService workOutRecordService,
                              RoutineGroupService routineGroupService,
                              RoutineService routineService) {
        this.calendarService = calendarService;
        this.workOutRecordService = workOutRecordService;
        this.routineGroupService = routineGroupService;
        this.routineService = routineService;
    }
    //달력 메인
    @GetMapping("/calender")
    public String calender(HttpSession session, Model model){

        Member member = (Member) session.getAttribute("loginMember");
        List<Map<String, String>> assignedDates = calendarService.findByMember(member).stream()
                .map(c -> Map.of(
                        "date", c.getDate().toString(),
                        "title", c.getRoutineGroup().getTitle()
                ))
                .toList();
        model.addAttribute("assignedDates", assignedDates);
        return "calender/calender";
    }

    //날짜 선택 & 루틴 그룹 설정
    @GetMapping("/select")
    public String selectRoutine(@RequestParam String date, Model model, HttpSession session){
        Member member = (Member)session.getAttribute("loginMember");

        Calender calender = calendarService.findByMemberAndDate(member, LocalDate.parse(date));
        if(calender != null){
            return"redirect:/calender/detail?date=" + date;
        }
        model.addAttribute("groups",routineGroupService.findByMember(member));
        model.addAttribute("date", date);

        return "calender/selectGroup";
    }

    //날짜에 루틴 그룹 배정
    @PostMapping("/assign")
    public String assignGroup(@RequestParam String date,
                              @RequestParam Long groupId,
                              HttpSession session){
        Member member = (Member)session.getAttribute("loginMember");
        RoutineGroup group = routineGroupService.findById(groupId);

        Calender calender = new Calender();
        LocalDate localDate = LocalDate.parse(date);
        calender.setDate(LocalDate.parse(date));
        if (calendarService.existsByMemberAndDate(member, localDate)) {
            return "redirect:/calender/select?date=" + date + "&duplicate=true";
        }

        calender.setDate(localDate);
        calender.setMember(member);
        calender.setRoutineGroup(group);

        calendarService.save(calender);
        return  "redirect:/calender/detail?date=" +date;
    }
    // 날짜 상세기록
    @GetMapping("/detail")
    public String detail(@RequestParam String date, HttpSession session, Model model){
        Member member = (Member)session.getAttribute("loginMember");
        Calender calender =  calendarService.findByMemberAndDate(member, LocalDate.parse(date));

        model.addAttribute("calender",calender);
        model.addAttribute("records.",workOutRecordService.findByCalender(calender));
        model.addAttribute("date",date);
        return "calender/detail";
    }
    //운동기록 저장
    @PostMapping("/record/save")
    public String saveRecord(@RequestParam Long calenderId,
                             @RequestParam String  workout,
                             @RequestParam int sets,
                             @RequestParam int reps,
                             @RequestParam(required = false) Integer weight){
        WorkOutRecord record = new WorkOutRecord();
        record.setWorkOut(workout);
        record.setReps(reps);
        record.setSets(sets);
        record.setWeight(weight);
        record.setCalender(calendarService.findById(calenderId));

        workOutRecordService.save(record);

        return "redirect:/calender/detail?date="+ record.getCalender().getDate();
    }

}
