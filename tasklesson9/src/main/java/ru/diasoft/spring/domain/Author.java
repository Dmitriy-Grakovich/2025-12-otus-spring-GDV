package ru.diasoft.spring.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Author {
    private Long id;
    private String lastName;
    private String FirstName;
    private Integer age;

}