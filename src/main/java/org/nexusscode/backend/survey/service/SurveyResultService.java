package org.nexusscode.backend.survey.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.survey.domain.DeveloperType;
import org.nexusscode.backend.survey.domain.DiscType;
import org.nexusscode.backend.survey.domain.SurveyResult;
import org.nexusscode.backend.survey.dto.DevSurveyResponseDto;
import org.nexusscode.backend.survey.dto.DiscSurveyResponseDto;
import org.nexusscode.backend.survey.dto.SurveyRequestDto;
import org.nexusscode.backend.survey.dto.SurveyResponseDto;
import org.nexusscode.backend.survey.repository.DeveloperTypeRepository;
import org.nexusscode.backend.survey.repository.DiscTypeRepository;
import org.nexusscode.backend.survey.repository.SurveyResultRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurveyResultService {
    private final SurveyResultRepository surveyResultRepository;
    private final UserService userService;
    private final DiscTypeRepository discTypeRepository;
    private final DeveloperTypeRepository developerTypeRepository;

    private static final List<Integer> D_TYPE_QUESTIONS = List.of(1, 2, 3, 4, 5);
    private static final List<Integer> I_TYPE_QUESTIONS = List.of(6, 7, 8, 9, 10);
    private static final List<Integer> S_TYPE_QUESTIONS = List.of(11, 12, 13, 14, 15);
    private static final List<Integer> C_TYPE_QUESTIONS = List.of(16, 17, 18, 19, 20);

    private static final List<Integer> DEV_APPROACH_QUESTIONS = List.of(21, 22, 23, 24, 25);
    private static final List<Integer> TEAM_COLLAB_QUESTIONS = List.of(26, 27, 28, 29, 30);
    private static final List<Integer> PROBLEM_SOLVING_QUESTIONS = List.of(31, 32, 33, 34, 35);
    private static final List<Integer> DEV_VALUES_QUESTIONS = List.of(36, 37, 38, 39, 40);

    @Transactional
    public void submitSurvey(Long userId, List<SurveyRequestDto> surveyRequestDtoList) {
        User user = userService.findById(userId);
        SurveyResult result = findByUser(user);
        if(result!=null){
            throw new CustomException(ErrorCode.ALREADY_EXISTENCE_SURVEY_RESULT);
        }
        // 각 유형별 점수 계산
        int dScore = calculateScoreForQuestions(surveyRequestDtoList, D_TYPE_QUESTIONS);
        int iScore = calculateScoreForQuestions(surveyRequestDtoList, I_TYPE_QUESTIONS);
        int sScore = calculateScoreForQuestions(surveyRequestDtoList, S_TYPE_QUESTIONS);
        int cScore = calculateScoreForQuestions(surveyRequestDtoList, C_TYPE_QUESTIONS);

        // 개발자 영역별 점수 계산
        int devApproachScore = calculateScoreForQuestions(surveyRequestDtoList, DEV_APPROACH_QUESTIONS);
        int teamCollabScore = calculateScoreForQuestions(surveyRequestDtoList, TEAM_COLLAB_QUESTIONS);
        int problemSolvingScore = calculateScoreForQuestions(surveyRequestDtoList, PROBLEM_SOLVING_QUESTIONS);
        int devValuesScore = calculateScoreForQuestions(surveyRequestDtoList, DEV_VALUES_QUESTIONS);

        DiscType discType = determineDiscType(dScore,iScore,sScore,cScore);
        DeveloperType developerType = determineDeveloperType(
            devApproachScore, teamCollabScore, problemSolvingScore, devValuesScore
        );

        SurveyResult surveyResult = SurveyResult.builder()
            .user(user)
            .dominanceScore(dScore)
            .influenceScore(iScore)
            .steadinessScore(sScore)
            .conscientiousnessScore(cScore)
            .discType(discType)
            .developmentApproachScore(devApproachScore)
            .teamCollaborationScore(teamCollabScore)
            .problemSolvingScore(problemSolvingScore)
            .developmentValuesScore(devValuesScore)
            .developerType(developerType)
            .build();

        surveyResultRepository.save(surveyResult);
    }

    private DiscType determineDiscType(int dScore,int iScore,int sScore,int cScore) {
        Map<String, Integer> typeScores = new HashMap<>();
        typeScores.put("주도형(D)", dScore);
        typeScores.put("사교형(I)", iScore);
        typeScores.put("안정형(S)", sScore);
        typeScores.put("신중형(C)", cScore);

        int max = Collections.max(typeScores.values());

        String primaryType = typeScores.entrySet().stream()
            .filter(e -> e.getValue() == max)
            .map(Map.Entry::getKey)
            .sorted()
            .findFirst()
            .orElse(null);

        return discTypeRepository.findByName(primaryType)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DISC_TYPE));
    }

    private DeveloperType determineDeveloperType(int devApproach, int teamCollab, int problemSolving, int devValues){
        Map<String, Integer> devScores = new HashMap<>();
        devScores.put("실행 중심 개발자", devApproach);
        devScores.put("협업형 개발자", teamCollab);
        devScores.put("문제 해결형 개발자", problemSolving);
        devScores.put("가치 지향 개발자", devValues);

        int max = Collections.max(devScores.values());

        String primaryType = devScores.entrySet().stream()
            .filter(e -> e.getValue() == max)
            .map(Map.Entry::getKey)
            .sorted()
            .findFirst()
            .orElse(null);

        return developerTypeRepository.findByName(primaryType)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DEV_TYPE));
    }

    private int calculateScoreForQuestions(List<SurveyRequestDto> surveyRequestDtoList, List<Integer> questionIds) {
        return questionIds.stream()
            .mapToInt(id -> {
                // 해당 ID와 일치하는 질문 찾기
                for (SurveyRequestDto surveyRequestDto : surveyRequestDtoList) {
                    if (surveyRequestDto.getQuestionNo() == id) {
                        return surveyRequestDto.getScore();
                    }
                }
                return 0;
            })
            .sum();
    }

    public SurveyResponseDto getSurveyResult(Long userId) {
        User user = userService.findById(userId);
        SurveyResult surveyResult = surveyResultRepository.findByUser(user);
        return new SurveyResponseDto(surveyResult);
    }

    public DiscSurveyResponseDto getDiscSurveyResult(Long userId) {
        User user = userService.findById(userId);
        SurveyResult surveyResult = surveyResultRepository.findByUser(user);
        return new DiscSurveyResponseDto(surveyResult);
    }

    public DevSurveyResponseDto getDevSurveyResult(Long userId) {
        User user = userService.findById(userId);
        SurveyResult surveyResult = surveyResultRepository.findByUser(user);
        return new DevSurveyResponseDto(surveyResult);
    }

    public SurveyResult findById(Long id){
        return surveyResultRepository.findById(id).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_SURVEY_RESULT)
        );
    }

    public SurveyResult findByUser(User user) {
        SurveyResult result = surveyResultRepository.findByUser(user);
        if (result == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_SURVEY_RESULT);
        }
        return result;
    }

    @Transactional
    public void updateDiscSurvey(Long userId, List<SurveyRequestDto> surveyRequestDtos) {
        User user = userService.findById(userId);
        SurveyResult surveyResult = findByUser(user);
        int dScore = calculateScoreForQuestions(surveyRequestDtos, D_TYPE_QUESTIONS);
        int iScore = calculateScoreForQuestions(surveyRequestDtos, I_TYPE_QUESTIONS);
        int sScore = calculateScoreForQuestions(surveyRequestDtos, S_TYPE_QUESTIONS);
        int cScore = calculateScoreForQuestions(surveyRequestDtos, C_TYPE_QUESTIONS);

        DiscType discType = determineDiscType(dScore,iScore,sScore,cScore);

        surveyResult.updateDisc(dScore,iScore,sScore,cScore,discType);
        surveyResultRepository.save(surveyResult);
    }

    @Transactional
    public void updateDevSurvey(Long userId, List<SurveyRequestDto> surveyRequestDtos) {
        User user = userService.findById(userId);
        SurveyResult surveyResult = findByUser(user);
        int devApproachScore = calculateScoreForQuestions(surveyRequestDtos, DEV_APPROACH_QUESTIONS);
        int teamCollabScore = calculateScoreForQuestions(surveyRequestDtos, TEAM_COLLAB_QUESTIONS);
        int problemSolvingScore = calculateScoreForQuestions(surveyRequestDtos, PROBLEM_SOLVING_QUESTIONS);
        int devValuesScore = calculateScoreForQuestions(surveyRequestDtos, DEV_VALUES_QUESTIONS);

        DeveloperType developerType = determineDeveloperType(devApproachScore,teamCollabScore,problemSolvingScore,devValuesScore);

        surveyResult.updateDev(devApproachScore,teamCollabScore,problemSolvingScore,devValuesScore,developerType);
        surveyResultRepository.save(surveyResult);
    }
}
