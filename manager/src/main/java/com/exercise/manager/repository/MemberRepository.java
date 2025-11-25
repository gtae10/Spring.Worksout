package com.exercise.manager.repository;

import com.exercise.manager.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,String> {

    Optional<Member> findById(String id);
    List<Member> findAll();
    Optional<Member> findByName(String name);
}
