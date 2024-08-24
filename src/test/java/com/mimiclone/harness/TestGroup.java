package com.mimiclone.harness;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestGroup {

    private int tgId;
    private String testType;
    private String parameterSet;
    private List<TestCase> tests = new ArrayList<>();

}
