package com.req2res.actionarybe.domain.todo.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;   // 이 날짜부터 카테고리가 보이기 시작(비즈니스 기준)

    //----메소드----
    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }
}
