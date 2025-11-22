package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.repository.CandidaturaRepository;
import org.springframework.stereotype.Service;

@Service
public class CandidaturaServiceImpl {

    private final CandidaturaRepository candidaturaRepository;

    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
    }

    @Override

}
