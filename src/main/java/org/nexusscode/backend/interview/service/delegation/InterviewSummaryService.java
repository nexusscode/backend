package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.nexusscode.backend.interview.repository.InterviewSummaryRepository;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewSummaryService {
    private final InterviewSummaryRepository interviewSummaryRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final UserRepository userRepository;

    public InterviewSummary createSummary(Long sessionId, Long userId, String summaryText) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        /*User user = userRepository.findById(userId).orElseThrow(
                        () -> new CustomException(ErrorCode.NOT_FOUND)
        );*/

        InterviewSummary summary = InterviewSummary.builder()
                .session(session)
            //    .user(user)
                .summary(summaryText)
                .build();

        return interviewSummaryRepository.save(summary);
    }

    public InterviewSummary createSummary(InterviewSession session, String context) {
        InterviewSummary interviewSummary = InterviewSummary.createInterviewSummary(session, context);

        try {
            return interviewSummaryRepository.save(interviewSummary);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SUMMARY_SAVE_FAILED);
        }
    }

    public void updateSummary(Long summaryId, String newSummaryText) {
        InterviewSummary summary = interviewSummaryRepository.findById(summaryId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        InterviewSummary updated = InterviewSummary.builder()
                .id(summary.getId())
                .session(summary.getSession())
                .summary(newSummaryText)
                .build();

        interviewSummaryRepository.save(updated);
    }

    @Cacheable(value = "interview:summary", key = "#sessionId")
    public Optional<String> findBySessionId(Long sessionId) {
        return interviewSummaryRepository.findSummaryBySessionId(sessionId);
    }
}
