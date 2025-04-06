package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.interview.client.GPTClient;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.dto.QuestionAndHintDTO;
import org.nexusscode.backend.interview.repository.InterviewQuestionRepository;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService{

    private final ResumeRepository resumeRepository;
    private final ResumeItemRepository resumeItemRepository;
    private final GPTClient gptClient;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    @Override
    public Long startInterview(String title, Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();
        List<ResumeItem> items = resumeItemRepository.findByResumeId(resume.getId());

        String resumeText = makeResumeText(items);
        List<InterviewQuestionDTO> gptQuestions = gptClient.generateInterviewQuestions(resumeText);
        
        List<InterviewQuestion> questions = mapToInterviewQuestions(gptQuestions);

        InterviewSession interviewSession = InterviewSession.createInterviewSession(
                resume.getApplication(), title, questions
        );

        Long sessionId = interviewSessionRepository.save(interviewSession).getId();

        return sessionId;
    }

    private String makeResumeText(List<ResumeItem> items) {
        StringBuilder sb = new StringBuilder();
        for (ResumeItem item : items) {
            sb.append("Q: ").append(item.getQuestion()).append("\n");
            sb.append("A: ").append(item.getAnswer()).append("\n\n");
        }
        return sb.toString();
    }

    private List<InterviewQuestion> mapToInterviewQuestions(List<InterviewQuestionDTO> dtos) {
        AtomicInteger counter = new AtomicInteger(0);

        return dtos.stream()
                .map(dto -> InterviewQuestion.builder()
                        .questionText(dto.getQuestion())
                        .intentText(dto.getIntent())
                        .seq(counter.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public QuestionAndHintDTO getQuestion(Long sessionId, Integer seq) {
        InterviewQuestion interviewQuestion = interviewQuestionRepository.findBySessionIdAndSeq(sessionId, seq).orElseThrow();

        return QuestionAndHintDTO.builder()
                .interviewQuestionId(interviewQuestion.getId())
                .questionText(interviewQuestion.getQuestionText())
                .intentText(interviewQuestion.getIntentText())
                .seq(interviewQuestion.getSeq())
                .build();
    }

    @Override
    public List<InterviewSessionDTO> getList() {
        return List.of();
    }
}

