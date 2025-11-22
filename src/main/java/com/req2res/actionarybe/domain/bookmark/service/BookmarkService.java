package com.req2res.actionarybe.domain.bookmark.service;

import org.springframework.stereotype.Service;

import com.req2res.actionarybe.domain.bookmark.repository.BookmarkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {

	private final BookmarkRepository bookmarkRepository;

}
