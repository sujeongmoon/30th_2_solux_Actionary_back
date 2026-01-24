package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoDTO {
    private int page;
    private int size;
    private Long totalElements;
    private int totalPages;
}
