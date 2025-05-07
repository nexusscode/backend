package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.AnswerFeedback;
import org.nexusscode.backend.interview.domain.InterviewAnswer;
import org.nexusscode.backend.interview.repository.AnswerFeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerFeedbackService {
    private final AnswerFeedbackRepository answerFeedbackRepository;

    public Long createFeedback(InterviewAnswer answer, String feedbackText, String blindKeywords) {
        if (feedbackText == null || feedbackText.isEmpty()) {
            throw new CustomException(ErrorCode.ANSWER_SAVE_FAILED);
        }
        AnswerFeedback feedback = AnswerFeedback.createAnswerFeedback(answer, feedbackText, blindKeywords);

        try {
            return answerFeedbackRepository.save(feedback).getId();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FEEDBACK_SAVE_FAILED);
        }
    }

    public Optional<AnswerFeedback> findByAnswerId(Long answerId) {
        return answerFeedbackRepository.findByAnswerId(answerId);
    }

    public void updateFeedback(Long feedbackId, String newFeedbackText, String newBlindKeywords) {
        AnswerFeedback feedback = answerFeedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        feedback = AnswerFeedback.builder()
                .id(feedback.getId())
                .answer(feedback.getAnswer())
                .feedbackText(newFeedbackText)
                .blindKeywords(newBlindKeywords)
                .build();

        answerFeedbackRepository.save(feedback);
    }

    public void deleteFeedback(Long feedbackId) {
        answerFeedbackRepository.deleteById(feedbackId);
    }
}
