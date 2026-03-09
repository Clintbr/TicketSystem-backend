package com.service.springbackend.ai;

import com.service.springbackend.model.Priority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketPriorityService {

    private final List<String> urgentKeywords = List.of(
            "server down",
            "system down",
            "production",
            "data loss",
            "security",
            "payment failed",
            "cannot login",
            "critical"
    );

    private final List<String> highKeywords = List.of(
            "bug",
            "error",
            "not working",
            "cannot complete",
            "failed"
    );

    private final List<String> mediumKeywords = List.of(
            "problem",
            "issue",
            "unexpected",
            "slow"
    );

    private final List<String> lowKeywords = List.of(
            "ui",
            "cosmetic",
            "typo",
            "suggestion",
            "improvement"
    );

    public Priority analyzePriority(String text) {

        text = text.toLowerCase();

        int score = 0;

        score += checkKeywords(text, urgentKeywords, 10);
        score += checkKeywords(text, highKeywords, 6);
        score += checkKeywords(text, mediumKeywords, 3);
        score += checkKeywords(text, lowKeywords, 1);

        if (score >= 10) return Priority.URGENT;
        if (score >= 6) return Priority.HIGH;
        if (score >= 3) return Priority.MEDIUM;

        return Priority.LOW;
    }

    private int checkKeywords(String text, List<String> keywords, int weight) {

        int score = 0;

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                score += weight;
            }
        }

        return score;
    }
}
