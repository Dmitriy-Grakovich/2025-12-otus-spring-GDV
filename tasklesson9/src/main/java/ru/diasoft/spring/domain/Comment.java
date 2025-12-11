package ru.diasoft.spring.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment {
    private Long id;
    private String description;
    private String nickname;

}
