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
public class TaskState {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private Long ordinal;
    private Instant createdAt = Instant.now();
    private String description;
    @OneToMany
    @JoinColumn(name = "task_state_id", referencedColumnName = "id")
    private List<Task> tasks = new ArrayList<>();
}
