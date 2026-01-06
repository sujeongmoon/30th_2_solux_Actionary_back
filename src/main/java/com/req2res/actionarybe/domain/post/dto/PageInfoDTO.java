package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfoDTO {
    private int pageNum;
    private int pageSize;
    private Long totalElements;
    private int totalPages;
}
