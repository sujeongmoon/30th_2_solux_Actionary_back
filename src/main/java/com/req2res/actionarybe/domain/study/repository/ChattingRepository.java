package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.study.entity.Chatting;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
}
