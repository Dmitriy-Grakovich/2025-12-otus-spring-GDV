package ru.diasoft.tasklesson16.controller.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDto {
    private Long id;
    private String title;
    private AuthorDto author;
    private GenreDto genre;
    private List<CommentDto> comments;
}
