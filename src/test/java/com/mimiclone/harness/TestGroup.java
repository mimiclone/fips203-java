package com.mimiclone.harness;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestGroup {

    private int tgId;
    private String testType;
    private String parameterSet;
    private String function;
    private String ek;
    private String dk;
    private List<TestCase> tests = new ArrayList<>();

}
