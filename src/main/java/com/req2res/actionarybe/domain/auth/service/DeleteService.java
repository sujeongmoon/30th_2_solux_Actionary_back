package com.req2res.actionarybe.domain.auth.service;

import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteService {
    public final MemberRepository memberRepository;

    public void withdrawMember(Long id){
        memberRepository.deleteById(id);
    }
}
