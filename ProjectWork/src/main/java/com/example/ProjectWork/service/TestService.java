package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Test;

import java.util.List;

public interface TestService {

    List<Test> getAllTests();
    Test getTestById(Long id);
    Test createTest(Test newTest);
    Test updateTest(Long id, Test test);
    void deleteTest(Long id);

}
