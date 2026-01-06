package com.req2res.actionarybe.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SortedResponseDTO {
    private List<PostSummaryDTO> posts;
    private PageInfoDTO pageInfo;
}

