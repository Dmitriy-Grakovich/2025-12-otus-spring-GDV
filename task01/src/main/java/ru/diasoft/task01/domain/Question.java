package ru.diasoft.task01.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Question {
    private final String text;

    private final List<Answer> answers;
    private final boolean IsMultiChoice;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(text).append("\n");
        for (int i = 0; i < answers.size(); i++) {
            sb.append(i + 1).append(". ").append(answers.get(i)).append("\n");
        }
        return sb.toString();
    }
}
