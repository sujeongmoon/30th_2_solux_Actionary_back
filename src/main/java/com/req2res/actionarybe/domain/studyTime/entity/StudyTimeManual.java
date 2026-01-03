package com.req2res.actionarybe.domain.studyTime.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "study_time_manual")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudyTimeManual extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "manual_date", nullable = false)
    private LocalDate manualDate;

    @Column(name = "duration_second", nullable = false)
    private Integer durationSecond;

    public void updateDurationSecond(Integer durationSecond) {
        this.durationSecond = durationSecond;
    }

    public void updateManualDate(LocalDate manualDate) {
        this.manualDate = manualDate;
    }
}
