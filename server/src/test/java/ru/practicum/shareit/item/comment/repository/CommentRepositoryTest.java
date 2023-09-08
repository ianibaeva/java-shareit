package ru.practicum.shareit.item.comment.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;
    private Item item;
    private Comment comment;

    @BeforeEach
    private void init() {
        user = new User();
        user.setName("name");
        user.setEmail("email@email.com");

        item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment.setText("comment");

        testEntityManager.persist(user);
        testEntityManager.persist(item);
        testEntityManager.persist(comment);
        testEntityManager.flush();
    }

    @AfterEach
    private void deleteAll() {
        commentRepository.deleteAll();
    }

    @Test
    void testFindAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(1L);

        assertEquals(1, comments.size());
        assertEquals("comment", comments.get(0).getText());
    }
}
