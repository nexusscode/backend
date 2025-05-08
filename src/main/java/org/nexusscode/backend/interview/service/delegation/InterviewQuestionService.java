package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewType;
import org.nexusscode.backend.interview.repository.InterviewQuestionRepository;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewQuestionService {
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewSessionRepository interviewSessionRepository;

    public InterviewQuestion saveQuestion(Long sessionId, String questionText, String intentText, String ttsUrl, InterviewType type, int seq) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        InterviewQuestion question = InterviewQuestion.builder()
                .session(session)
                .questionText(questionText)
                .intentText(intentText)
                .TTSFileName(ttsUrl)
                .interviewType(type)
                .seq(seq)
                .build();

        return interviewQuestionRepository.save(question);
    }

    @Transactional
    public void updateQuestion(InterviewQuestion question) {
        interviewQuestionRepository.updateTTSUrlById(question.getId(), question.getTTSFileName());
    }

    public Optional<InterviewQuestion> findById(Long questionId) {
        return interviewQuestionRepository.findById(questionId);
    }

    public Optional<List<InterviewQuestion>> findBySessionId(Long sessionId) {
        return interviewQuestionRepository.findBySessionId(sessionId);
    }

    public void deleteQuestion(Long questionId) {
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        interviewQuestionRepository.delete(question);
    }

    public void updateSession(Long questionId, Long newSessionId) {
        InterviewQuestion question = interviewQuestionRepository.findById(questionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        InterviewSession session = interviewSessionRepository.findById(newSessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        question.saveSession(session);
    }

    public Optional<InterviewQuestion> findQuestionAndHint(Long sessionId, Integer seq) {
        return interviewQuestionRepository.findBySessionIdAndSeq(sessionId, seq);
    }
}
