package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private ItemRequest itemRequest;

    @BeforeEach
    private void addItems() {
        User user = new User();
        user.setName("name");
        user.setEmail("email@email.com");
        testEntityManager.persist(user);

        itemRequest = new ItemRequest();
        itemRequest.setDescription("TestRequestDescription");
        itemRequest.setRequestorId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        testEntityManager.persist(itemRequest);

        Item item = new Item();
        item.setName("TestItem");
        item.setDescription("TestItemDescription");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(itemRequest);
        testEntityManager.persist(item);

        testEntityManager.flush();
    }

    @AfterEach
    private void deleteAll() {
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(1L);

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "TestItem");
    }

    @Test
    void testSearchAvailableItemsByNameOrDescription() {
        String searchText = "test";

        List<Item> items = itemRepository.searchAvailableItemsByNameOrDescription(searchText, Sort.by(Sort.Order.asc("id")));

        assertEquals(1, items.size());
        assertEquals("TestItem", items.get(0).getName());
    }

    @Test
    void testFindAllByRequestId() {
        List<Item> items = itemRepository.findAllByRequest_Id(itemRequest.getId());

        assertEquals(1, items.size());
        assertEquals("TestItem", items.get(0).getName());
    }
}

