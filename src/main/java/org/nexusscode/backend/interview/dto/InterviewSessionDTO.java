package org.nexusscode.backend.interview.dto;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSessionDTO {
    private Long sessionId;
    private String title;
}
