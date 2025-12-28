package com.req2res.actionarybe.domain.member.service;

import com.req2res.actionarybe.domain.member.dto.LoginMemberResponseDTO;
import com.req2res.actionarybe.domain.member.dto.UpdateNicknameResponseDTO;
import com.req2res.actionarybe.domain.member.dto.UpdateProfileRequestDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Member findMemberByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

    public LoginMemberResponseDTO getLoginMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 회원이 없습니다."));
        return new LoginMemberResponseDTO(
                member.getId(),
                member.getImageUrl(),
                member.getNickname(),
                member.getPhoneNumber(),
                member.getBirthday()
        );
    }

    // 프로필 사진 변경
    @Transactional
    public UpdateProfileRequestDTO updateProfile(Long id, String imageUrl){
        Member member=memberRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setImageUrl(imageUrl);

        return new UpdateProfileRequestDTO(
                member.getImageUrl() // @Transactional: '메서드' 단위 -> 위에서 set한 현재값 바로 반영됨 (이전값 반영 걱정X)
        );
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
}
