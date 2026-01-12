package ru.diasoft.tasklesson16.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;
    @Column(name = "description", nullable = false, unique = true)
    private String description;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book; // Добавляем связь с книгой


}
