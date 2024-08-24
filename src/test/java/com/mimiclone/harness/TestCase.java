package com.mimiclone.harness;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Data
public class TestCase {

    private int tcId;

    private Map<String,Object> values = new HashMap<>();

    @JsonAnySetter
    public void setValues(String name, Object value) {
        values.put(name, value);
    }

}
