package com.req2res.actionarybe.domain.search.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostSearchPageResponseDTO {
    private List<PostSearchResponseDTO> content;
    private PageInfoDTO pageInfo;
}
