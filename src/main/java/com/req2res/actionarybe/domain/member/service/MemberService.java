package com.req2res.actionarybe.domain.member.service;

import com.req2res.actionarybe.domain.auth.service.AuthService;
import com.req2res.actionarybe.domain.image.service.ImageService;
import com.req2res.actionarybe.domain.member.dto.*;
import com.req2res.actionarybe.domain.member.entity.Badge;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
    private final AuthService authService;
    private final ImageService imageService;

    public Member findMemberByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

    // 로그인 유저 정보 조회
    public LoginMemberResponseDTO getLoginMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 회원이 없습니다."));

        return new LoginMemberResponseDTO(
                member.getId(),
                member.getProfileImageUrl(),
                authService.chooseNickname(member),
                member.getPhoneNumber(),
                member.getBirthday()
        );
    }

    // 타인정보 조회
    public OtherMemberResponseDTO getOtherMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return new OtherMemberResponseDTO(
                member.getId(),
                authService.chooseNickname(member),
                member.getProfileImageUrl()
        );
    }

    // 프로필 사진 변경
    @Transactional
    public void updateProfile(Long id, MultipartFile profileImage){
        Member member=memberRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (profileImage == null || profileImage.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }
        // S3 업로드 → URL 생성
        String imageUrl = imageService.saveImage(profileImage);

        member.setProfileImageUrl(imageUrl);
    }

    // 닉네임 변경
    @Transactional
    public UpdateNicknameResponseDTO updateNickname(Long id, String nickname){
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        member.setNickname(nickname);

        return new UpdateNicknameResponseDTO(
                member.getId(),
                member.getNickname()
        );
    }

    // 회원 뱃지 정보 조회
    @Transactional
    public BadgeResponseDTO badge(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Badge badge = Optional.ofNullable(member.getBadge())
                .orElseThrow(()->new CustomException(ErrorCode.BADGE_NOT_ASSIGNED));

        return new BadgeResponseDTO(
                badge.getId(),
                memberId,
                badge.getName(),
                badge.getRequiredPoint(),
                badge.getImageUrl()
        );
    }
}
