package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private Booking pastBooking;
    private Booking futureBooking;

    @BeforeEach
    public void init() {
        user = new User();
        user.setName("name");
        user.setEmail("email@email.com");

        owner = new User();
        owner.setName("name2");
        owner.setEmail("email2@email.com");

        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusHours(1L));
        booking.setEnd(LocalDateTime.now().plusDays(1L));

        pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(user);
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setStart(LocalDateTime.now().minusHours(1L));
        pastBooking.setEnd(LocalDateTime.now().plusDays(1L));

        futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setStart(LocalDateTime.now().plusDays(1L));
        futureBooking.setEnd(LocalDateTime.now().plusDays(2L));

        testEntityManager.persist(user);
        testEntityManager.persist(owner);
        testEntityManager.persist(item);
        testEntityManager.flush();
        bookingRepository.save(booking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
    }

    @AfterEach
    public void deleteAll() {
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findAllByBookerId(1L, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void findAllCurrentBookingsByBookerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByBookerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertTrue(booking.getStart().isBefore(now) || booking.getStart().isEqual(now));
            assertTrue(booking.getEnd().isAfter(now) || booking.getEnd().isEqual(now));
        }
    }

    @Test
    void findAllPastBookingsByBookerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllPastBookingsByBookerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertTrue(booking.getEnd().isBefore(now));
        }
    }

    @Test
    void findAllFutureBookingsByBookerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllFutureBookingsByBookerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertTrue(booking.getStart().isAfter(now));
        }
    }

    @Test
    void findAllWaitingBookingsByBookerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllWaitingBookingsByBookerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(Status.WAITING, booking.getStatus());
            assertTrue(booking.getStart().isAfter(now));
        }
    }

    @Test
    void findAllRejectedBookingsByBookerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllRejectedBookingsByBookerId(userId, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(Status.REJECTED, booking.getStatus());
        }
    }

    @Test
    void findAllByOwnerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllByOwnerId(userId, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getItem().getOwner().getId());
        }
    }

    @Test
    void findAllCurrentBookingsByOwnerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllCurrentBookingsByOwnerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getItem().getOwner().getId());
            assertTrue(booking.getStart().isBefore(now) || booking.getStart().isEqual(now));
            assertTrue(booking.getEnd().isAfter(now) || booking.getEnd().isEqual(now));
        }
    }

    @Test
    void findAllPastBookingsByOwnerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllPastBookingsByOwnerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getItem().getOwner().getId());
            assertTrue(booking.getEnd().isBefore(now));
        }
    }

    @Test
    void findAllFutureBookingsByOwnerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllFutureBookingsByOwnerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getItem().getOwner().getId());
            assertTrue(booking.getStart().isAfter(now));
        }
    }

    @Test
    void findAllWaitingBookingsByOwnerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllWaitingBookingsByOwnerId(userId, now, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getItem().getOwner().getId());
            assertEquals("WAITING", booking.getStatus().name());
            assertTrue(booking.getStart().isAfter(now));
        }
    }

    @Test
    void findAllRejectedBookingsByOwnerId_ReturnsCorrectBookings() {
        Long userId = user.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());

        List<Booking> bookings = bookingRepository.findAllRejectedBookingsByOwnerId(userId, pageable);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getItem().getOwner().getId());
            assertEquals("REJECTED", booking.getStatus().name());
        }
    }

    @Test
    void findAllByUserBookings_ReturnsCorrectBookings() {
        Long userId = user.getId();
        Long itemId = item.getId();
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findAllByUserBookings(userId, itemId, now);

        assertThat(bookings).isNotNull();
        for (Booking booking : bookings) {
            assertEquals(userId, booking.getBooker().getId());
            assertEquals(itemId, booking.getItem().getId());
            assertEquals("APPROVED", booking.getStatus().name());
            assertTrue(booking.getEnd().isBefore(now));
        }
    }

    @Test
    void getLastBooking_ReturnsCorrectBooking() {
        Long itemId = item.getId();
        LocalDateTime currentTime = LocalDateTime.now();

        Optional<Booking> optionalBooking = bookingRepository.getLastBooking(itemId, currentTime);
        assertThat(optionalBooking).isPresent();

        Booking booking = optionalBooking.get();

        assertThat(booking.getId()).isEqualTo(pastBooking.getId());
        assertThat(booking.getStatus()).isEqualTo(pastBooking.getStatus());
        assertFalse(booking.getStart().isAfter(currentTime));
    }

    @Test
    void getNextBooking_ReturnsCorrectBooking() {
        Long itemId = item.getId();
        LocalDateTime currentTime = LocalDateTime.now();

        Optional<Booking> optionalBooking = bookingRepository.getNextBooking(itemId, currentTime);
        assertThat(optionalBooking).isPresent();

        Booking booking = optionalBooking.get();

        assertThat(booking.getId()).isEqualTo(futureBooking.getId());
        assertThat(booking.getStatus()).isEqualTo(futureBooking.getStatus());
        assertTrue(booking.getStart().isAfter(currentTime));
    }
}
