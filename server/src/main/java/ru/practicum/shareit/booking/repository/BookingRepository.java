package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(Long userId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingsByBookerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastBookingsByBookerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingsByBookerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = 'WAITING' " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllWaitingBookingsByBookerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findAllRejectedBookingsByBookerId(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerId(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = :userId " +
            "AND :now BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = :userId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = :userId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = :userId " +
            "AND b.status = 'WAITING' " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findAllWaitingBookingsByOwnerId(Long userId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE i.owner.id = :userId " +
            "AND b.status = 'REJECTED' " +
            "ORDER BY b.start DESC")
    List<Booking> findAllRejectedBookingsByOwnerId(Long userId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN Item i ON i.id = b.item.id " +
            "WHERE b.booker.id = :userId " +
            "AND i.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now ")
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);

    @Query(value = "SELECT * FROM bookings b " +
            "JOIN items i ON i.id = b.item_id " +
            "WHERE b.item_id = :itemId " +
            "AND b.start_date <= :currentTime " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date DESC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getLastBooking(Long itemId, LocalDateTime currentTime);

    @Query(value = "SELECT * FROM bookings b " +
            "JOIN items i ON i.id = b.item_id " +
            "WHERE b.item_id = :itemId " +
            "AND b.start_date > :currentTime " +
            "AND b.status = 'APPROVED' " +
            "ORDER BY b.start_date ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getNextBooking(Long itemId, LocalDateTime currentTime);

    List<Booking> findAllByItemInAndStatus(List<Item> items, Status status, Sort sort);
}
