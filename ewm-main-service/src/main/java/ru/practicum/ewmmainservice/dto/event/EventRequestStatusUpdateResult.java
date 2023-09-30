package ru.practicum.ewmmainservice.dto.event;

import lombok.*;
import ru.practicum.ewmmainservice.dto.request.ParticipationRequestDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {

    @NotBlank
    private List<ParticipationRequestDto> confirmedRequests;

    @NotBlank
    private List<ParticipationRequestDto> rejectedRequests;
}