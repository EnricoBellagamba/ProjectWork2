package com.example.ProjectWork.dto.candidatura;

import java.util.List;

public class Top5Request {

    private List<Long> top5;

    public Top5Request() {}

    public List<Long> getTop5() {
        return top5;
    }

    public void setTop5(List<Long> top5) {
        this.top5 = top5;
    }
}
