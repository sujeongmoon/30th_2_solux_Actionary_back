package com.req2res.actionarybe.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateNicknameRequestDTO {

    @NotBlank(message = "loginId는 필수입니다.")
    @Schema(example = "new_nickname")
    String nickname;

}
