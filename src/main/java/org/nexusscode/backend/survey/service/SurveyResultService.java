package org.nexusscode.backend.survey.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.survey.dto.SureveyRequestDto;
import org.nexusscode.backend.survey.repository.SurveyResultRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveyResultService {
    private final SurveyResultRepository surveyResultRepository;

    private static final List<Integer> D_TYPE_QUESTIONS = List.of(1, 2, 3, 4, 5);
    private static final List<Integer> I_TYPE_QUESTIONS = List.of(6, 7, 8, 9, 10);
    private static final List<Integer> S_TYPE_QUESTIONS = List.of(11, 12, 13, 14, 15);
    private static final List<Integer> C_TYPE_QUESTIONS = List.of(16, 17, 18, 19, 20);

    private static final List<Integer> DEV_APPROACH_QUESTIONS = List.of(21, 22, 23, 24, 25);
    private static final List<Integer> TEAM_COLLAB_QUESTIONS = List.of(26, 27, 28, 29, 30);
    private static final List<Integer> PROBLEM_SOLVING_QUESTIONS = List.of(31, 32, 33, 34, 35);
    private static final List<Integer> DEV_VALUES_QUESTIONS = List.of(36, 37, 38, 39, 40);

    public void submitSurvey(SureveyRequestDto sureveyRequestDto) {
        // 각 유형별 점수 계산
        int dScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), D_TYPE_QUESTIONS);
        int iScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), I_TYPE_QUESTIONS);
        int sScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), S_TYPE_QUESTIONS);
        int cScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), C_TYPE_QUESTIONS);

        // 개발자 영역별 점수 계산
        int devApproachScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), DEV_APPROACH_QUESTIONS);
        int teamCollabScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), TEAM_COLLAB_QUESTIONS);
        int problemSolvingScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), PROBLEM_SOLVING_QUESTIONS);
        int devValuesScore = calculateScoreForQuestions(sureveyRequestDto.getAnswers(), DEV_VALUES_QUESTIONS);

        // 주요 유형과 부수적 유형 결정
        Map<String, Integer> typeScores = new HashMap<>();
        typeScores.put("D", dScore);
        typeScores.put("I", iScore);
        typeScores.put("S", sScore);
        typeScores.put("C", cScore);

        List<Map.Entry<String, Integer>> sortedTypes = typeScores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());

        String primaryType = sortedTypes.get(0).getKey();
        String secondaryType = sortedTypes.get(1).getKey();

        SurveyResult surveyResult = SurveyResult.builder()
            .dominanceScore(dScore)
            .influenceScore(iScore)
            .steadinessScore(sScore)
            .conscientiousnessScore(cScore)
            .primaryType(primaryType)
            .secondaryType(secondaryType)
            .developmentApproachScore(devApproachScore)
            .teamCollaborationScore(teamCollabScore)
            .problemSolvingScore(problemSolvingScore)
            .developmentValuesScore(devValuesScore)
            .build();

        surveyResultRepository.save(surveyResult);
    }

    private int calculateScoreForQuestions(Map<Integer, Integer> answers, List<Integer> questionIds) {
        return questionIds.stream()
            .mapToInt(id -> {
                Integer answer = answers.get(id);
                // "그렇다"(4), "대체로 그렇다"(3), "대체로 그렇지 않다"(2), "그렇지 않다"(1)로 매핑
                return answer != null ? answer : 0;
            })
            .sum();
    }
}
