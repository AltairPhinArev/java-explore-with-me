package ru.practicum.ewmmainservice.dto.user;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    private Long id;

    @NotBlank
    private String name;
}
