package com.exercise.manager.controller;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.WeightLog;
import com.exercise.manager.dto.HeatmapDay;
import com.exercise.manager.dto.PrEntry;
import com.exercise.manager.service.CalenderService;
import com.exercise.manager.service.RoutineService;
import com.exercise.manager.service.WeightLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatsController {

    private final CalenderService calenderService;
    private final RoutineService routineService;
    private final WeightLogService weightLogService;

    public StatsController(CalenderService calenderService, RoutineService routineService, WeightLogService weightLogService) {
        this.calenderService = calenderService;
        this.routineService = routineService;
        this.weightLogService = weightLogService;
    }

    @GetMapping("/stats")
    public String stats(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return "redirect:/login";

        List<Calender> all = calenderService.findByMember(loginMember);
        all.sort((a, b) -> b.getDate().compareTo(a.getDate())); // 최신순

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate monthStart = today.withDayOfMonth(1);

        long thisWeekCount = all.stream().filter(c -> !c.getDate().isBefore(weekStart)).count();
        long thisMonthCount = all.stream().filter(c -> !c.getDate().isBefore(monthStart)).count();
        long totalCount = all.size();

        // ---------- 부위별 빈도 ----------
        Map<String, Long> partFrequency = all.stream()
                .filter(c -> c.getRoutineGroup() != null)
                .flatMap(c -> c.getRoutineGroup().getDistinctParts().stream())
                .collect(Collectors.groupingBy(p -> p, LinkedHashMap::new, Collectors.counting()));
        List<Map.Entry<String, Long>> sortedParts = partFrequency.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());
        long maxPartCount = sortedParts.isEmpty() ? 1 : sortedParts.get(0).getValue();

        // ---------- 부위 밸런스 경고 (최근 30일간 한 번도 안 한 부위) ----------
        LocalDate last30 = today.minusDays(30);
        Set<String> recentParts = all.stream()
                .filter(c -> !c.getDate().isBefore(last30) && c.getRoutineGroup() != null)
                .flatMap(c -> c.getRoutineGroup().getDistinctParts().stream())
                .collect(Collectors.toSet());
        List<String> allTrackedParts = List.of("가슴", "등", "어깨", "팔", "하체");
        List<String> missingParts = allTrackedParts.stream()
                .filter(p -> !recentParts.contains(p))
                .collect(Collectors.toList());

        // ---------- 연속 운동일수(스트릭) ----------
        List<LocalDate> distinctDates = all.stream().map(Calender::getDate).distinct().sorted().collect(Collectors.toList());
        int longestStreak = 0;
        int run = 0;
        LocalDate prev = null;
        for (LocalDate d : distinctDates) {
            run = (prev != null && d.equals(prev.plusDays(1))) ? run + 1 : 1;
            longestStreak = Math.max(longestStreak, run);
            prev = d;
        }
        Set<LocalDate> dateSet = new HashSet<>(distinctDates);
        LocalDate cursor = dateSet.contains(today) ? today : today.minusDays(1);
        int currentStreak = 0;
        while (dateSet.contains(cursor)) {
            currentStreak++;
            cursor = cursor.minusDays(1);
        }

        // ---------- 요일별 운동 패턴 (월~일 고정 순서) ----------
        String[] dowLabel = {"월", "화", "수", "목", "금", "토", "일"};
        DayOfWeek[] dowOrder = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        Map<DayOfWeek, Long> dowCount = all.stream()
                .collect(Collectors.groupingBy(c -> c.getDate().getDayOfWeek(), Collectors.counting()));
        List<Map.Entry<String, Long>> dowFrequency = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dowFrequency.add(Map.entry(dowLabel[i], dowCount.getOrDefault(dowOrder[i], 0L)));
        }
        long maxDowCount = dowFrequency.stream().mapToLong(Map.Entry::getValue).max().orElse(1);
        if (maxDowCount == 0) maxDowCount = 1;

        // ---------- 가장 많이 한 운동 TOP 5 (배정된 날짜 기준 빈도) ----------
        Map<String, Long> exerciseFrequency = all.stream()
                .filter(c -> c.getRoutineGroup() != null)
                .flatMap(c -> c.getRoutineGroup().getRoutines().stream())
                .collect(Collectors.groupingBy(Routine::getWorkout, LinkedHashMap::new, Collectors.counting()));
        List<Map.Entry<String, Long>> topExercises = exerciseFrequency.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        // ---------- 이번 달 히트맵 ----------
        YearMonth ym = YearMonth.from(today);
        List<HeatmapDay> heatmap = new ArrayList<>();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate d = ym.atDay(day);
            heatmap.add(new HeatmapDay(day, dateSet.contains(d), d.equals(today)));
        }

        // ---------- 최근 활동 / PR ----------
        List<Calender> recent = all.stream().limit(8).collect(Collectors.toList());
        List<PrEntry> topPrs = routineService.findPersonalRecords(loginMember).stream()
                .limit(5).collect(Collectors.toList());

        // ---------- 체중 변화 추이 ----------
        // WeightLog 엔티티를 그대로 JS로 넘기면 연관된 Member(비밀번호 해시 포함)까지 직렬화될 위험이 있어서
        // date/weight만 담은 안전한 DTO로 변환해서 넘김
        List<com.exercise.manager.dto.WeightPoint> weightHistory = weightLogService.findByMember(loginMember).stream()
                .map(w -> new com.exercise.manager.dto.WeightPoint(w.getDate(), w.getWeight()))
                .collect(Collectors.toList());

        model.addAttribute("thisWeekCount", thisWeekCount);
        model.addAttribute("thisMonthCount", thisMonthCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("partFrequency", sortedParts);
        model.addAttribute("maxPartCount", maxPartCount);
        model.addAttribute("missingParts", missingParts);
        model.addAttribute("currentStreak", currentStreak);
        model.addAttribute("longestStreak", longestStreak);
        model.addAttribute("dowFrequency", dowFrequency);
        model.addAttribute("maxDowCount", maxDowCount);
        model.addAttribute("topExercises", topExercises);
        model.addAttribute("heatmap", heatmap);
        model.addAttribute("currentMonthLabel", ym.getMonthValue() + "월");
        model.addAttribute("recent", recent);
        model.addAttribute("topPrs", topPrs);
        model.addAttribute("weightHistory", weightHistory);

        return "stats/stats";
    }
}
