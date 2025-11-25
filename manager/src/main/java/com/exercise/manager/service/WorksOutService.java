package com.exercise.manager.service;

import com.exercise.manager.domain.WorksOut;
import com.exercise.manager.repository.WorksOutRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorksOutService {

    private final WorksOutRepository worksOutRepository;
    public WorksOutService(WorksOutRepository worksOutRepository){
        this.worksOutRepository = worksOutRepository;
    }

    public List<WorksOut> findAll(){
        return worksOutRepository.findAll();
    }

    public List<WorksOut> findByPart(String part){
        List<WorksOut> list;
        if (part.equals("전체")) {
            list = worksOutRepository.findAll();
        } else {
            list = worksOutRepository.findByPart(part);
        }
        return list;
    }

    public Optional<WorksOut> findById(Long id){
        return worksOutRepository.findById(id);
    }
}
