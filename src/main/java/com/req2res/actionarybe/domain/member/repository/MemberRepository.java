package com.req2res.actionarybe.domain.member.repository;

import com.req2res.actionarybe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginIdAndWithdrawnTrue(String loginId); // 이미 탈퇴한 로그인id는 다시 사용 불가
    boolean existsByLoginId(String loginId);
}
