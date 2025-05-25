package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.nexusscode.backend.interview.dto.InterviewSummaryDTO;
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

    public InterviewSummary createSummary(InterviewSession session, InterviewSummaryDTO context) {
        InterviewSummary interviewSummary = InterviewSummary.createInterviewSummary(session, context);

        try {
            return interviewSummaryRepository.save(interviewSummary);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SUMMARY_SAVE_FAILED);
        }
    }

    @Cacheable(value = "interview:summary", key = "#sessionId")
    public Optional<InterviewSummary> findBySessionId(Long sessionId) {
        return interviewSummaryRepository.findSummaryBySessionId(sessionId);
    }
}
