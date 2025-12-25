package com.req2res.actionarybe.domain.auth.service;

import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteService {
    public final MemberRepository memberRepository;

    public void withdrawMember(Long id){
        if(!memberRepository.existsById(id)){
            throw new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다: " + id);
        }
        memberRepository.deleteById(id);
    }
}
