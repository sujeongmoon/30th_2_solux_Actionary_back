package com.req2res.actionarybe.domain.comment.dto;

import com.req2res.actionarybe.domain.post.dto.PageInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetCommentResponseDTO {
    List<CommentInfoDTO> comments;
    private PageInfoDTO pageInfo;
}
