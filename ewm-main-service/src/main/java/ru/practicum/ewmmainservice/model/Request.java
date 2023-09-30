package ru.practicum.ewmmainservice.model;

import lombok.*;
import ru.practicum.ewmmainservice.dto.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column
    private LocalDateTime created;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
}
