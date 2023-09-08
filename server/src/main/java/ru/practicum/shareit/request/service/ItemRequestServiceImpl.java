package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Constant;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.mapper.ItemRequestMapper.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto addNewRequest(Long userId, ItemRequestDto requestDto) {
        Long userExists = userRepository.countById(userId);

        if (userExists == 0) {
            throw new ObjectNotFoundException("User not found");
        }

        ItemRequest request = toItemRequest(requestDto);
        request.setRequestorId(userId);

        return toItemRequestDto(requestRepository.save(request));
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Request with ID: %s " +
                        "not found", requestId)));

        return getRequestDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorId(userId);

        return requests.stream()
                .map(this::getRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Pageable page = PageRequest.of(from / size, size, Constant.SORT_BY_CREATED_ASC);

        return requestRepository.findAllByRequestorIdIsNot(userId, page).stream()
                .map(this::getRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestResponseDto getRequestDto(ItemRequest request) {
        List<ItemDtoForRequests> items = itemRepository.findAllByRequest_Id(request.getRequestorId()).stream()
                .map(ItemMapper::toItemDtoShort)
                .collect(Collectors.toList());

        return toItemRequestResponseDto(request, items);
    }
}
