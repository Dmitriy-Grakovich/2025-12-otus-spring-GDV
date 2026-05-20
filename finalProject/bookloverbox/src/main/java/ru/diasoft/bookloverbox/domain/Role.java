package ru.diasoft.bookloverbox.domain;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"users"})
@ToString(exclude = {"users"})
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(length = 255)
    private String description;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
    
    // Константы для удобства
    public static final String READER = "ROLE_READER";
    public static final String AUTHOR = "ROLE_AUTHOR";
    public static final String MODERATOR = "ROLE_MODERATOR";
    public static final String ADMIN = "ROLE_ADMIN";
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
