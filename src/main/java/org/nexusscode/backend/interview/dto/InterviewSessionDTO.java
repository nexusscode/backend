package org.nexusscode.backend.interview.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSessionDTO {
    private Long sessionId;
    private String title;
    private LocalDateTime createdAt;
}
