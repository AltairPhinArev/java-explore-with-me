package ru.practicum.ewmmainservice.dto.comment;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDtoToCreateAndUpdate {

    @NotBlank
    @Length(min = 1, max = 512)
    String description;
}
