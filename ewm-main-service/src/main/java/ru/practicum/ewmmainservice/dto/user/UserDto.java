package ru.practicum.ewmmainservice.dto.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank
    @Length(min = 2, max = 250)
    private String name;

    @Email
    @NotBlank
    @Length(min = 6, max = 254)
    private String email;
}