package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUserItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/" + itemId, userId, null);
    }

    public ResponseEntity<Object> getAvailableItemBySearch(Long userId, String text) {
        if (text.isBlank()) return ResponseEntity.ok(List.of());
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addItem(ItemDto itemDto, Long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> createComment(Long userId, CommentDto commentRequestDto, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentRequestDto);
    }

    public ResponseEntity<Object> removeItem(Long itemId) {
        return delete("/" + itemId);
    }
}
