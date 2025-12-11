package ru.diasoft.spring.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {
    private Long id;
    private String title;
    private Author author;
    private Genre genre;
    private List<Comment> comments;
}
