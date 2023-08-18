package ru.practicum.shareit.util;

import org.springframework.data.domain.Sort;

public class Constant {
    public static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";
    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
}
