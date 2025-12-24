package com.req2res.actionarybe.domain.todo.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todo_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoCategory extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 카테고리 id

    @Column(name = "user_id", nullable = false)
    private Long userId;   // 카테고리 소유 사용자

    @Column(nullable = false)
    private String name;   // 카테고리 이름

    @Column
    private String color;
}
