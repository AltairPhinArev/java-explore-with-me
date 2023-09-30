package ru.practicum.ewmmainservice.dto.compilation;

import lombok.*;
import ru.practicum.ewmmainservice.model.Event;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private Long id;

    private Boolean pinned;

    @NotBlank
    @Size(max = 50)
    private String title;

    private Set<Event> events;
}

