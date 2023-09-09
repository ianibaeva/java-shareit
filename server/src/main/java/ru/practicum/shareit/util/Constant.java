package ru.practicum.shareit.util;

import org.springframework.data.domain.Sort;

import static org.springframework.data.domain.Sort.Direction.DESC;

public class Constant {
    public static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";
    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
    public static final Sort SORT_BY_CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");
    public static final Sort SORT_BY_START_DESC = Sort.by(DESC, "start");
    public static final Sort SORT_BY_CREATED_ASC = Sort.by(Sort.Direction.ASC, "created");
    public static final Sort SORT_BY_DESC_START = Sort.by(Sort.Direction.DESC, "start");
}
