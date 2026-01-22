package com.req2res.actionarybe.domain.todo.repository;

import java.time.LocalDate;

public interface TodoDoneCountByDate {
    LocalDate getDate();
    Long getDoneCount();
}

