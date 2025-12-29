package com.req2res.actionarybe.domain.study.entity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.global.Timestamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
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
public class Study extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column
	private String coverImage;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Column
	private String description;

	@Column(nullable = false)
	private int memberLimit;

	@Column
	private Boolean isPublic;

	@Column //암호화
	private String password;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_user_id")
	private Member creator;

	public void updateStudy(@Valid StudyRequestDto request, Member member) {

		String encodedPassword = null;

		if (request.getPassword() != null && !request.getPassword().equals("")) {
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			encodedPassword = encoder.encode(request.getPassword());
		}

		this.name = request.getStudyName();
		this.coverImage = request.getCoverImage();
		this.category = request.getCategory();
		this.description = request.getDescription();
		this.memberLimit = request.getMemberLimit();
		this.isPublic = request.getIsPublic();
		this.password = encodedPassword;
		this.creator = member;
	}
}

