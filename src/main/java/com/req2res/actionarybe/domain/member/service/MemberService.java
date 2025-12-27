package com.req2res.actionarybe.domain.member.service;

import com.req2res.actionarybe.domain.member.dto.LoginMemberResponseDTO;
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
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
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
}
