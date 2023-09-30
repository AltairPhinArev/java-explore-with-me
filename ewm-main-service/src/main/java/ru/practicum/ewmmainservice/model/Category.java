package ru.practicum.ewmmainservice.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;
}
