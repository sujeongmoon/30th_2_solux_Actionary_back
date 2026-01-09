package com.req2res.actionarybe.domain.member.repository;

import com.req2res.actionarybe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginIdAndWithdrawnFalse(String loginId); // 탈퇴 회원 id/pw는 타인이 쓸 수 있음

//    boolean existsByLoginId(String loginId);
}
