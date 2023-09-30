package ru.practicum.ewmmainservice.model;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private Double lat;

    private Double lon;
}