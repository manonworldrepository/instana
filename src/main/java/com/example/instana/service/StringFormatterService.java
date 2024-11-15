package com.example.instana.service;

import com.example.instana.model.ResultRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StringFormatterService {

    public String formatResultRecord(ResultRecord resultRecord) {
        return resultRecord.id() + ". " + resultRecord.result();
    }

    public String formatAllResults(List<String> results) {
        return "\n\nGraph results:\n" + String.join("\n", results);
    }
}
