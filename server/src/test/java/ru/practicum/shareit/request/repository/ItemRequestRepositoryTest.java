package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequest createItemRequest(User requestor, String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequestorId(requestor.getId());
        itemRequest.setDescription(description);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    @BeforeEach
    private void init() {
        User user1 = createUser("name", "email@email.com");
        User user2 = createUser("name2", "email2@email.com");
        testEntityManager.persist(user1);
        testEntityManager.persist(user2);
        testEntityManager.flush();

        ItemRequest itemRequest1 = createItemRequest(user1, "request description");
        ItemRequest itemRequest2 = createItemRequest(user2, "request2 description");
        testEntityManager.persist(itemRequest1);
        testEntityManager.persist(itemRequest2);
    }

    @Test
    void findAllByRequestorIdOrderByCreated() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorId(1L);

        assertEquals(1, requests.size());
        assertEquals("request description", requests.get(0).getDescription());
    }

    @Test
    void findAllByRequestorIdNot() {
        Page<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdIsNot(2L, PageRequest.of(0, 1));

        assertEquals(1, requests.getTotalElements());
        assertEquals("request description", requests.getContent().get(0).getDescription());
    }
}

