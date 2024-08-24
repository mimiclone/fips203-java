package com.mimiclone.harness;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestPrompt {

    private int vsId;
    private String algorithm;
    private String mode;
    private String revision;

    @JsonProperty("isSample")
    private boolean sample;

    private List<TestGroup> testGroups = new ArrayList<>();

}
