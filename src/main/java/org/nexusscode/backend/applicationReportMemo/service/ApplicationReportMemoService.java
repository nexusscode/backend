package org.nexusscode.backend.applicationReportMemo.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.applicationReportMemo.domain.ApplicationReportMemo;
import org.nexusscode.backend.applicationReportMemo.domain.ReportMemoInputSet;
import org.nexusscode.backend.applicationReportMemo.dto.ReportMemoInputSetRequest;
import org.nexusscode.backend.applicationReportMemo.dto.ReportMemoInputSetResponse;
import org.nexusscode.backend.applicationReportMemo.dto.ReportMemoAnalysisResponse;
import org.nexusscode.backend.applicationReportMemo.repository.ApplicationReportMemoRepository;
import org.nexusscode.backend.applicationReportMemo.repository.ReportMemoInputSetRepository;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.client.GPTClient;
import org.nexusscode.backend.interview.service.support.PromptType;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.nexusscode.backend.interview.service.support.InterviewServiceUtil.buildPrompt;

@Service
@RequiredArgsConstructor
public class ApplicationReportMemoService {

    private final ApplicationReportMemoRepository applicationReportMemoRepository;
    private final ReportMemoInputSetRepository reportMemoInputSetRepository;
    private final UserRepository userRepository;
    private final GPTClient gptClient;

    @Transactional
    public ReportMemoInputSetResponse saveUserInput(Long userId, ReportMemoInputSetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        ApplicationReportMemo reportMemo = ApplicationReportMemo.builder()
                .user(user)
                .build();
        ApplicationReportMemo savedReportMemo = applicationReportMemoRepository.save(reportMemo);


        List<ReportMemoInputSet> inputSets = request.getMemoList().stream()
                .map(memo -> ReportMemoInputSet.builder()
                        .user(user)
                        .question(memo.getQuestion())
                        .answer(memo.getAnswer())
                        .applicationReportMemo(savedReportMemo)
                        .build())
                .collect(Collectors.toList());
        List<ReportMemoInputSet> savedInputSets = reportMemoInputSetRepository.saveAll(inputSets);


        List<ReportMemoInputSetResponse.MemoOutput> memoOutputs = savedInputSets.stream()
                .map(input -> new ReportMemoInputSetResponse.MemoOutput(
                        input.getId(),
                        input.getQuestion(),
                        input.getAnswer()
                ))
                .collect(Collectors.toList());

        return new ReportMemoInputSetResponse(userId, memoOutputs);
    }

    @Transactional
    public ReportMemoAnalysisResponse getAnalysisForGpt(Long userId, Long reportMemoId) {

        ApplicationReportMemo reportMemo = applicationReportMemoRepository.findById(reportMemoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<ReportMemoInputSet> inputSets = reportMemo.getInputSetList();
        if (inputSets.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        String inputText = makeTextFromInputSets(inputSets);
        System.out.println("=== 입력된 질문과 답변 ===");
        System.out.println(inputText);
        System.out.println("=====================");

        PromptTemplate promptTemplate = buildPrompt(PromptType.REPORT_MEMO, 0);
        Map<String, String> analysisResult = gptClient.generateReportMemoAnalysis(inputText, promptTemplate);
        
        System.out.println("=== GPT 분석 결과 ===");
        System.out.println("강점/약점: " + analysisResult.getOrDefault("prosAndCons", ""));
        System.out.println("분석 결과: " + analysisResult.getOrDefault("analysisResult", ""));
        System.out.println("=================");
        
        if (analysisResult == null || analysisResult.isEmpty()) {
            throw new CustomException(ErrorCode.GPT_ANALYSIS_FAILED);
        }

        String prosAndCons = analysisResult.getOrDefault("prosAndCons", "");
        String analysisResultText = analysisResult.getOrDefault("analysisResult", "");
        
        // System.out.println("=== 저장할 데이터 ===");
        // System.out.println("강점/약점: " + prosAndCons);
        // System.out.println("분석 결과: " + analysisResultText);
        // System.out.println("=================");

        reportMemo.updateAnalysisResult(prosAndCons, analysisResultText);
        applicationReportMemoRepository.save(reportMemo);
        
        // System.out.println("=== 저장된 데이터 ===");
        // System.out.println("강점/약점: " + reportMemo.getProsAndCons());
        // System.out.println("분석 결과: " + reportMemo.getAnalysisResult());
        // System.out.println("=================");

        return ReportMemoAnalysisResponse.builder()
                .prosAndCons(prosAndCons)
                .analysisResult(analysisResultText)
                .build();
    }

    private String makeTextFromInputSets(List<ReportMemoInputSet> inputSets) {
        StringBuilder text = new StringBuilder();
        for (ReportMemoInputSet input : inputSets) {
            text.append("질문: ").append(input.getQuestion()).append("\n");
            text.append("답변: ").append(input.getAnswer()).append("\n\n");
        }
        return text.toString();
    }
}
