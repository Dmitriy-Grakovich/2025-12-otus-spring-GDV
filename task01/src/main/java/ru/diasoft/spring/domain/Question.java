package ru.diasoft.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Question {
    private final String text;

    private final Map<Integer, Answer> answers;
    private final boolean IsMultiChoice;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(text).append("\n");
        for (Map.Entry<Integer, Answer> entry : answers.entrySet()) {
            sb.append(entry.getKey()).append(". ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
