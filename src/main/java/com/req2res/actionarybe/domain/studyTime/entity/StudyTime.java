package com.req2res.actionarybe.domain.studyTime.entity;

import com.req2res.actionarybe.domain.study.entity.StudyParticipant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_participant_id", nullable = false)
	private StudyParticipant studyParticipant;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Type type;

	@Column
	private int durationSecond;
}
