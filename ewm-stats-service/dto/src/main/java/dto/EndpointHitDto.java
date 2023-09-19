package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHitDto {

    private Long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}