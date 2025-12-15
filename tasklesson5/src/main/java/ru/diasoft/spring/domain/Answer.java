package ru.diasoft.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class Answer {
    private final String value;
    private final boolean isCorrect;


    @Override
    public String toString() {

        return value;
    }
}
