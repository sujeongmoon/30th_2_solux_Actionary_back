package com.req2res.actionarybe.domain.search.dto;

import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "검색 정렬 기준")
public enum SearchSort {
    RECENT,
    POPULAR;

    public static SearchSort from(String value) {
        // sort 파라미터가 없으면 기본값
        if (value == null || value.isBlank()) {
            return RECENT;
        }

        try {
            return SearchSort.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(
                    ErrorCode.BAD_REQUEST,
                    "정렬 값이 올바르지 않습니다. (RECENT 또는 POPULAR)"
            );
        }
    }
}
