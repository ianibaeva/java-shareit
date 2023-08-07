package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "text", nullable = false, length = 2000)
    String text;
    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    Item item;
    @OneToOne
    @JoinColumn(name = "author_id", nullable = false)
    User author;
    LocalDateTime created;
}
