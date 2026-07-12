package com.exercise.manager.service;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineGroup;
import com.exercise.manager.repository.CalenderRepository;
import com.exercise.manager.repository.MemberRepository;
import com.exercise.manager.repository.RoutineGroupRepository;
import com.exercise.manager.repository.RoutineRepository;
import com.exercise.manager.repository.WorkOutRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CalenderRepository calenderRepository;
    private final WorkOutRecordRepository workOutRecordRepository;
    private final RoutineRepository routineRepository;
    private final RoutineGroupRepository routineGroupRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         CalenderRepository calenderRepository,
                         WorkOutRecordRepository workOutRecordRepository,
                         RoutineRepository routineRepository,
                         RoutineGroupRepository routineGroupRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.calenderRepository = calenderRepository;
        this.workOutRecordRepository = workOutRecordRepository;
        this.routineRepository = routineRepository;
        this.routineGroupRepository = routineGroupRepository;
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

    // 체중 저장/수정 (칼로리 계산용)
    public void updateWeight(Member member, double weight) {
        member.setWeight(weight);
        memberRepository.save(member);
    }

    // 프로필(이름/체중/키) 수정
    public void updateProfile(Member member, String name, Double weight, Double height) {
        member.setName(name);
        member.setWeight(weight);
        member.setHeight(height);
        memberRepository.save(member);
    }

    // 회원 탈퇴: 이 회원과 관련된 모든 데이터를 FK 제약을 고려한 순서로 정리한 뒤 회원 자체를 삭제
    @Transactional
    public void deleteAccount(Member member) {
        // 1) 캘린더에 딸린 세트 기록부터 정리
        List<Calender> calenders = calenderRepository.findByMember(member);
        for (Calender c : calenders) {
            workOutRecordRepository.deleteAll(workOutRecordRepository.findByCalender(c));
        }
        calenderRepository.deleteAll(calenders);

        // 2) 루틴(운동)들 정리 - RoutineSet은 Routine에 cascade+orphanRemoval 걸려있어서 같이 삭제됨
        List<Routine> routines = routineRepository.findByMember(member);
        routineRepository.deleteAll(routines);

        // 3) 루틴 그룹 정리
        List<RoutineGroup> groups = routineGroupRepository.findByMember(member);
        routineGroupRepository.deleteAll(groups);

        // 4) 회원 삭제
        memberRepository.delete(member);
    }

    // 루틴 데이터를 CSV로 내보내기 (백업/제출용)
    @Transactional
    public String exportRoutinesCsv(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append("루틴그룹,운동명,부위,세트,반복,무게(kg),볼륨(kg)\n");

        for (RoutineGroup g : routineGroupRepository.findByMember(member)) {
            for (Routine r : g.getRoutines()) {
                sb.append(csvEscape(g.getTitle())).append(",")
                  .append(csvEscape(r.getWorkout())).append(",")
                  .append(csvEscape(r.getPart())).append(",")
                  .append(r.getTotalSetsCount()).append(",")
                  .append(r.getTotalRepsCount()).append(",")
                  .append(r.getWeight()).append(",")
                  .append(r.getVolume())
                  .append("\n");
            }
        }
        return sb.toString();
    }

    private String csvEscape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

}
