package org.nexusscode.backend.interview.dto;

import lombok.*;
import org.nexusscode.backend.interview.domain.InterviewSession;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAllSessionDTO {
    private Long sessionId;
    private String title;
    private List<InterviewQnADTO> questions;
    private String summary;
    private int countSeconds;

    public static InterviewAllSessionDTO createAllSessionDTO(InterviewSession session, List<InterviewQnADTO> questions, String summary) {
        InterviewAllSessionDTO build = InterviewAllSessionDTO.builder()
                .sessionId(session.getId())
                .title(session.getTitle())
                .questions(questions)
                .summary(summary)
                .build();

        build.calculateCountSeconds();

        return build;
    }

    private void calculateCountSeconds() {
        this.countSeconds = questions.stream().mapToInt(InterviewQnADTO::getSecond).sum();
    }
}
