package org.nexusscode.backend.interview.service;

import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.dto.QuestionAndHintDTO;

import java.util.List;

public interface InterviewService {

    Long startInterview(String title, Long resumeId);

    List<InterviewSessionDTO> getList();

    QuestionAndHintDTO getQuestion(Long sessionId, Integer seq);
}
