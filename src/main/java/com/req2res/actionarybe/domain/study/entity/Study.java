package com.req2res.actionarybe.domain.study.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.dto.StudyRequestDto;
import com.req2res.actionarybe.global.Timestamped;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyLike> studyLikes = new ArrayList<>();

	public void updateStudy(@Valid StudyRequestDto request, Member member, String coverImageUrl) {

		String encodedPassword = null;

		if (request.getPassword() != null && !request.getPassword().equals("")) {
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			encodedPassword = encoder.encode(request.getPassword());
		}

		this.name = request.getStudyName();
		this.coverImage = coverImageUrl;
		this.category = request.getCategory();
		this.description = request.getDescription();
		this.memberLimit = request.getMemberLimit();
		this.isPublic = request.getIsPublic();
		this.password = encodedPassword;
		this.creator = member;
	}
}
