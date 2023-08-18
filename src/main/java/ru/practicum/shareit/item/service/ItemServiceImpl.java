package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.item.comment.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.comment.mapper.CommentMapper.toCommentDto;
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
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = toItem(itemDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));
        item.setOwner(user);

        return toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isEmpty()) {
            throw new ObjectNotFoundException(
                    String.format("Item with ID %s not found", itemId));
        }

        if (!itemOptional.get().getOwner().getId().equals(userId)) {
            throw new ForbiddenException(String.format("User with ID %s " +
                    "is not the owner of item with ID %s.", userId, itemId));
        }

        Item item = toItem(itemDto);

        if (Objects.isNull(item.getName())) {
            item.setName(itemOptional.get().getName());
        }

        if (Objects.isNull(item.getDescription())) {
            item.setDescription(itemOptional.get().getDescription());
        }

        if (Objects.isNull(item.getAvailable())) {
            item.setAvailable(itemOptional.get().getAvailable());
        }
        item.setOwner(itemOptional.get().getOwner());
        item.setRequest(itemOptional.get().getRequest());
        item.setId(itemOptional.get().getId());

        return toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("User with ID: %s " +
                        "does not have an item with ID: %s.", userId, itemId)));

        ItemDto itemDto = toItemDto(item);
        itemDto.setComments(getAllComments(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }

        getLastBooking(itemDto);
        getNextBooking(itemDto);

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllUserItems(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        List<ItemDto> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        items.forEach(i -> {
            getLastBooking(i);
            getNextBooking(i);
            i.setComments(getAllComments(i.getId()));
        });

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAvailableItemBySearch(String text, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return itemRepository.findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CommentDto> getAllComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, CommentDto commentDto, Long itemId) {
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

        return toCommentDto(commentRepository.save(toComment(commentDto, item, user)));
    }

    private void getLastBooking(ItemDto itemDto) {
        Optional<Booking> lastBooking = bookingRepository.getLastBooking(itemDto.getId(), LocalDateTime.now());
        if (lastBooking.isPresent()) {
            itemDto.setLastBooking(toBookingItemDto(lastBooking.get()));
        } else {
            itemDto.setLastBooking(null);
        }
    }

    private void getNextBooking(ItemDto itemDto) {
        Optional<Booking> nextBooking = bookingRepository.getNextBooking(itemDto.getId(), LocalDateTime.now());
        if (nextBooking.isPresent()) {
            itemDto.setNextBooking(toBookingItemDto(nextBooking.get()));
        } else {
            itemDto.setNextBooking(null);
        }
    }
}
