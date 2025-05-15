package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewQnADTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private final InterviewSessionRepository interviewSessionRepository;

    public InterviewSession createSession(String title, List<InterviewQuestion> questions, JobApplication application, GptVoice interviewType) {
        InterviewSession session = InterviewSession.createInterviewSession(application, title, questions, interviewType);

        try {
            InterviewSession result = interviewSessionRepository.save(session);

            return result;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SESSION_SAVE_FAILED);
        }
    }

    public void addQuestions(Long sessionId, List<InterviewQuestion> questions) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        session.addQuestion(questions);
    }

    public boolean saveSessionToArchive(Long sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        session.saveSessionToArchive();

        return true;
    }

    public boolean deleteSessionFromArchive(Long sessionId) {
        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        session.deleteSessionToArchive();

        return true;
    }

    public Optional<InterviewSession> findById(Long sessionId) {
        return interviewSessionRepository.findById(sessionId);
    }

    @Cacheable(value = "interview:sessionList", key = "#applicationId")
    public Optional<List<InterviewSessionDTO>> findSessionList(Long applicationId) {
        return interviewSessionRepository.findSessionListByApplicationId(applicationId);
    }

    @Cacheable(value = "interview:sessionQnA", key = "#sessionId")
    public Optional<List<InterviewQnADTO>> findInterviewQnA(Long sessionId) {
        return findInterviewQnAFromDB(sessionId);
    }

    public Optional<List<InterviewQnADTO>> findInterviewQnAFromDB(Long sessionId) {
        return interviewSessionRepository.findInterviewQnABySessionId(sessionId);
    }

    public Optional<List<InterviewSessionDetailDto>> getSessionDetails(Long sessionId) {
        return interviewSessionRepository.findInterviewSessionDetail(sessionId);
    }

    public Optional<List<InterviewAdviceDTO>> getInterviewAdvice(Long sessionId) {
        return interviewSessionRepository.findInterviewAdviceBySessionId(sessionId);
    }

    public Boolean deleteSession(Long sessionId) {
        interviewSessionRepository.deleteById(sessionId);
        return true;
    }
}
