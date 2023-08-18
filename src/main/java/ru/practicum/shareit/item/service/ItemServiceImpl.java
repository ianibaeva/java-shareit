package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Constant;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.item.comment.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemResponseDto addItem(ItemDtoOut itemDtoOut, Long userId) {
        Item item = toItem(itemDtoOut);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
        item.setOwner(user);

        return toItemDto(itemRepository.save(item), null, null, null);
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long itemId, ItemDtoOut itemDtoOut, Long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isEmpty()) {
            throw new ObjectNotFoundException(
                    String.format("Item with ID %s not found", itemId));
        }

        if (!itemOptional.get().getOwner().getId().equals(userId)) {
            throw new ForbiddenException(String.format("User with ID %s " +
                    "is not the owner of item with ID %s.", userId, itemId));
        }

        Item item = toItem(itemDtoOut);

        if (Objects.isNull(item.getName()) || item.getName().isBlank()) {
            item.setName(itemOptional.get().getName());
        }

        if (Objects.isNull(item.getDescription()) || item.getDescription().isBlank()) {
            item.setDescription(itemOptional.get().getDescription());
        }

        if (Objects.isNull(item.getAvailable())) {
            item.setAvailable(itemOptional.get().getAvailable());
        }
        item.setOwner(itemOptional.get().getOwner());
        item.setRequest(itemOptional.get().getRequest());
        item.setId(itemOptional.get().getId());

        return toItemDto(itemRepository.save(item), null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with ID: %s " +
                        "does not have an item with ID: %s.", userId, itemId)));

        ItemResponseDto itemResponseDto = toItemDto(item, null, null, null);
        itemResponseDto.setComments(getAllComments(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            return itemResponseDto;
        }

        LocalDateTime currentTime = LocalDateTime.now();
        Optional<Booking> lastBooking = bookingRepository.getLastBooking(itemId, currentTime);
        lastBooking.ifPresent(booking -> itemResponseDto.setLastBooking(toBookingItemDto(booking)));

        Optional<Booking> nextBooking = bookingRepository.getNextBooking(itemId, currentTime);
        nextBooking.ifPresent(booking -> itemResponseDto.setNextBooking(toBookingItemDto(booking)));

        return itemResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllUserItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        List<ItemResponseDto> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map((Item item) -> ItemMapper.toItemDto(item, null, null, null))
                .collect(Collectors.toList());

        LocalDateTime currentTime = LocalDateTime.now();

        Map<Long, List<CommentResponseDto>> commentsMap = commentRepository.findByItemIdIn(
                items.stream().map(ItemResponseDto::getId).collect(Collectors.toList()),
                Constant.SORT_BY_CREATED_DESC
        ).stream().collect(Collectors.groupingBy(CommentResponseDto::getId));

        for (ItemResponseDto itemResponseDto : items) {
            Long itemId = itemResponseDto.getId();

            Optional<Booking> lastBooking = bookingRepository.getLastBooking(itemId, currentTime);
            lastBooking.ifPresent(booking -> itemResponseDto.setLastBooking(BookingMapper.toBookingItemDto(booking)));

            Optional<Booking> nextBooking = bookingRepository.getNextBooking(itemId, currentTime);
            nextBooking.ifPresent(booking -> itemResponseDto.setNextBooking(BookingMapper.toBookingItemDto(booking)));

            itemResponseDto.setComments(commentsMap.getOrDefault(itemId, Collections.emptyList()));
        }

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAvailableItemBySearch(String text, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.searchAvailableItemsByNameOrDescription(text, Constant.SORT_BY_ID_ASC)
                .stream()
                .map((Item item) -> ItemMapper.toItemDto(item, null, null, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CommentResponseDto> getAllComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, CommentRequestDto commentRequestDto, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Optional<Item> itemOptional = itemRepository.findById(itemId);

        Item item = itemOptional.orElseThrow(() -> new ObjectNotFoundException(String.format("User with ID: %s " +
                "does not have an item with ID: %s.", userId, itemId)));

        List<Booking> userBookings = bookingRepository.findAllByUserBookings(userId, itemId, LocalDateTime.now());

        if (userBookings.isEmpty()) {
            throw new ValidationException(String.format("User with ID %s must have at least one booking " +
                    "for the item with ID %s.", userId, itemId));
        }

        Comment newComment = toComment(commentRequestDto, item, user);
        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }
}
