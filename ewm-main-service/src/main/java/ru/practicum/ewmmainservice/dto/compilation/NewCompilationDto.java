package ru.practicum.ewmmainservice.dto.compilation;

import lombok.*;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    @NotBlank
    @Size(max = 50)
    private String title;
}
