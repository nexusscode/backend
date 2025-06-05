package org.nexusscode.backend.resume.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.ResumeItemFeedback;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.dto.ResumeItemFeedbackResponseDto;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.repository.ResumeItemFeedbackRepository;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.survey.service.SurveyResultService;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.nexusscode.backend.user.service.UserStatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeItemFeedbackService {

    private final UserService userService;
    private final ResumeService resumeService;
    private final SurveyResultService surveyResultService;
    private final ChatClient chatClient;
    private final ResumeItemRepository resumeItemRepository;
    private final ResumeItemFeedbackRepository resumeItemFeedbackRepository;
    private final ResumeFeedbackLimiterService resumeFeedbackLimiterService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserStatService userStatService;

    private static final PromptTemplate FEEDBACK_PROMPT_TEMPLATE = new PromptTemplate("""
        [자기소개서 문항 피드백 요청]

        <질문>
        {question}

        <답변>
        {answer}

        아래 조건에 맞추어 피드백을 해주세요.
        1. 질문과 답변의 관련성을 평가해 주세요.
        2. 맞춤법 및 문장 오류가 있다면 수정해주세요.
        3. 보다 명확하거나 인상적인 표현으로 개선해주세요.

        <공고 정보>
        - 회사명: {companyName}
        - 공고명: {jobTitle}
        - 공고유형: {jobCode}

        <사용자의 DISC 성격유형 검사 결과>
        - D형 점수: {dominanceScore}
        - I형 점수: {influenceScore}
        - S형 점수: {steadinessScore}
        - C형 점수: {conscientiousnessScore}
        - 주요 유형: {discType}

        <사용자의 개발자 특성 검사 결과>
        - 개발 접근 방식: {developmentApproachScore}
        - 팀 협업: {teamCollaborationScore}
        - 문제 해결: {problemSolvingScore}
        - 개발 가치관: {developmentValuesScore}
        - 주요 유형: {developType}

        [피드백 응답 형식 예시]
        - 질문 관련성 평가 : ...
        - 맞춤법 및 문장 수정 : ...
        - 표현 개선 제안 : ...
        - 사용자 성향 기반 피드백 : ...
        - 공고 정보 기반 피드백 : ...
        - 종합 평가 : ...
        """);

    public String createResumeFeedback(JobApplication application, String question, String answer) {
        try {
            SurveyResult surveyResult = surveyResultService.findByUser(application.getUser());

            Map<String, Object> variables = new HashMap<>();
            variables.put("companyName", application.getCompanyName());
            variables.put("jobTitle", application.getJobTitle());
            variables.put("jobCode", application.getJobCode());

            variables.put("dominanceScore", surveyResult.getDominanceScore());
            variables.put("influenceScore", surveyResult.getInfluenceScore());
            variables.put("steadinessScore", surveyResult.getSteadinessScore());
            variables.put("conscientiousnessScore", surveyResult.getConscientiousnessScore());
            variables.put("discType", surveyResult.getDiscType().getName());

            variables.put("developmentApproachScore", surveyResult.getDevelopmentApproachScore());
            variables.put("teamCollaborationScore", surveyResult.getTeamCollaborationScore());
            variables.put("problemSolvingScore", surveyResult.getProblemSolvingScore());
            variables.put("developmentValuesScore", surveyResult.getDevelopmentValuesScore());
            variables.put("developType", surveyResult.getDeveloperType().getName());

            variables.put("question", question);
            variables.put("answer", answer);

            String prompt = FEEDBACK_PROMPT_TEMPLATE.create(variables).toString();

            return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        } catch (Exception e) {
            throw new RuntimeException("자기소개서 피드백 생성 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResumeItemFeedbackResponseDto updateResumeItemFeedback(Long userId, Long resumeItemId, ResumeItemRequestDto resumeItemRequestDto) {
        User user = userService.findById(userId);
        ResumeItem resumeItem = resumeItemRepository.findById(resumeItemId).orElseThrow(
            ()-> new CustomException(ErrorCode.NOT_FOUND_RESUME_ITEM)
        );
        if(resumeItem.getResume().getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        resumeFeedbackLimiterService.checkLimit(user.getId());
        resumeItem.updateResumeItem(resumeItemRequestDto);
        resumeItem.updateAiCount();
        resumeItemRepository.save(resumeItem);
        String feedbackText = createResumeFeedback(resumeItem.getResume().getApplication(),resumeItemRequestDto.getQuestion(),resumeItemRequestDto.getAnswer());
        ResumeItemFeedback feedback = ResumeItemFeedback.builder()
            .resumeItem(resumeItem)
            .feedbackText(feedbackText)
            .build();
        resumeItemFeedbackRepository.save(feedback);
        resumeItem.getResume().updateAiCount();
        resumeService.save(resumeItem.getResume());
        userStatService.incrementResumeCount(userId);
        return new ResumeItemFeedbackResponseDto(feedback);
    }

    public ResumeItemFeedbackResponseDto getResumeItemLatestFeedback(Long userId,Long resumeItemId) {
        User user = userService.findById(userId);
        ResumeItem resumeItem = resumeItemRepository.findById(resumeItemId).orElseThrow(
            ()-> new CustomException(ErrorCode.NOT_FOUND_RESUME_ITEM)
        );
        if(resumeItem.getResume().getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        ResumeItemFeedback resumeItemFeedback = resumeItemFeedbackRepository.findTopByResumeItemOrderByCreatedAtDesc(resumeItem);
        return new ResumeItemFeedbackResponseDto(resumeItemFeedback);
    }

    public Long getRemainingCount(Long userId) {
        userService.findById(userId);
        String key = "resume_ai_feedback:" + userId + ":" + LocalDate.now();
        Number number = (Number) redisTemplate.opsForValue().get(key);
        long count = (number != null) ? number.longValue() : 0L;
        long remaining = Math.max(0, 20 - count);
        return remaining;
    }

    public Long getResumeFeedbackTotalCount(Long userId) {
        User user = userService.findById(userId);
        Long count = resumeService.sumAiCountByUser(user);
        return count;
    }
}

