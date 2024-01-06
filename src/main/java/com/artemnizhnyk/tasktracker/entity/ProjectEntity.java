package com.artemnizhnyk.tasktracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private Instant createdAt = Instant.now();
    @OneToMany
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private List<TaskState> taskStates = new ArrayList<>();
}
