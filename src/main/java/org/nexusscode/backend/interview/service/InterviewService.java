package org.nexusscode.backend.interview.service;

import org.nexusscode.backend.interview.dto.*;

import java.util.List;

public interface InterviewService {

    Long startInterview(InterviewStartRequest request);

    List<InterviewSessionDTO> getList(Long applicationId);

    QuestionAndHintDTO getQuestion(Long sessionId, Integer seq);

    Long submitAnswer(InterviewAnswerRequest request);

    InterviewAllSessionDTO getFullSessionDetail(Long sessionId);

    boolean saveSessionToArchive(Long sessionId);

    boolean deleteSessionToArchive(Long sessionId);

    String getUserVoicePreSignUrl(String fileName);

    String getAIVoicePreSignUrl(String fileName);
}
