package org.nexusscode.backend.resume.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.survey.service.SurveyResultService;
import org.nexusscode.backend.user.domain.User;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeFeedbackService {

    private final SurveyResultService surveyResultService;
    private final ChatClient chatClient;

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
        - 공고내용: {jobDescription}

        <DISC 성격유형 검사 결과>
        - D형 점수: {dominanceScore}
        - I형 점수: {influenceScore}
        - S형 점수: {steadinessScore}
        - C형 점수: {conscientiousnessScore}
        - 주요 유형: {primaryType}, 보조 유형: {secondaryType}

        <개발자 특성 검사 결과>
        - 개발 접근 방식: {developmentApproachScore}
        - 팀 협업: {teamCollaborationScore}
        - 문제 해결: {problemSolvingScore}
        - 개발 가치관: {developmentValuesScore}

        [피드백 응답 형식 예시]
        - 질문 관련성 평가: ...
        - 맞춤법 및 문장 수정: ...
        - 표현 개선 제안: ...
        - 종합 평가: ...
        """);

    public String getResumeFeedback(JobApplication application, String question, String answer) {
        try {
            SurveyResult surveyResult = surveyResultService.findByUser(application.getUser());

            Map<String, Object> variables = new HashMap<>();
            variables.put("companyName", application.getCompanyName());
            variables.put("jobTitle", application.getJobTitle());
            variables.put("jobCode", application.getJobCode());
            variables.put("jobDescription", application.getJobDescription());

            variables.put("dominanceScore", surveyResult.getDominanceScore());
            variables.put("influenceScore", surveyResult.getInfluenceScore());
            variables.put("steadinessScore", surveyResult.getSteadinessScore());
            variables.put("conscientiousnessScore", surveyResult.getConscientiousnessScore());
            variables.put("primaryType", surveyResult.getPrimaryType());
            variables.put("secondaryType", surveyResult.getSecondaryType());

            variables.put("developmentApproachScore", surveyResult.getDevelopmentApproachScore());
            variables.put("teamCollaborationScore", surveyResult.getTeamCollaborationScore());
            variables.put("problemSolvingScore", surveyResult.getProblemSolvingScore());
            variables.put("developmentValuesScore", surveyResult.getDevelopmentValuesScore());

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
}

