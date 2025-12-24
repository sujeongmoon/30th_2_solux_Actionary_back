package com.req2res.actionarybe.domain.Member.service;

import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.Member.entity.Member;
import com.req2res.actionarybe.domain.Member.repository.MemberRepository;
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
}
