package org.nexusscode.backend.interview.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class GPTClient {

    private final ChatClient chatClient;


    public List<InterviewQuestionDTO> generateInterviewQuestions(String inputText, PromptTemplate pro) {
        return parseQuestionsFrom(callClient(Map.of("inputText", inputText), pro));
    }

    public Map<String, String> generateSummary(String inputText, String trim, PromptTemplate pro) {
        return parseFeedback(callClient(Map.of("inputText", inputText, "previousAttemptText", trim), pro));
    }

    public Map<String, String> generateAdvice(String inputText, PromptTemplate pro) {
        return parseGptAdviceResponse(callClient(Map.of("inputText", inputText), pro));
    }

    private String callClient(Map inputText, PromptTemplate pro) {
        try {
            PromptTemplate promptTemplate = pro;
            String prompt = promptTemplate.create(inputText).toString();

            String content = chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            return content;
        }catch (Exception e) {
            log.error(e);
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }


    private List<InterviewQuestionDTO> parseQuestionsFrom(String gptOutput) {
        List<InterviewQuestionDTO> result = new ArrayList<>();

        String[] blocks = gptOutput.split("(?=\\*?\\*?\\s*질문\\s*\\d*\\s*:)");

        for (String block : blocks) {
            String question = null;
            String intent = null;

            if (block.strip().startsWith("###")) {
                continue;
            }

            for (String line : block.strip().split("\\r?\\n")) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("###")) {
                    continue;
                }

                if (line.matches(".*질문\\s*\\d*\\s*:\\s*.*")) {
                    question = line.replaceAll(".*질문\\s*\\d*\\s*:\\s*", "").trim();
                    question = cleanText(question);
                } else if (line.matches(".*의도:\\s*.*")) {
                    intent = line.replaceAll(".*의도:\\s*", "").trim();
                    intent = cleanText(intent);
                }
            }

            if (question != null && intent != null) {
                result.add(InterviewQuestionDTO.builder()
                        .question(question)
                        .intent(intent)
                        .build());
            }
        }
        return result;
    }

    private String cleanText(String text) {
        return text.replaceAll("^[\\-\\s\\*]+", "") // 앞쪽 -, 공백, * 제거
                .replaceAll("[\\-\\s\\*]+$", "") // 뒤쪽 -, 공백, * 제거
                .trim();
    }

    private Map<String, String> parseGptAdviceResponse(String responseText) {
        log.info(responseText);
        List<String> feedbackLines = new ArrayList<>();
        List<String> blindKeywords = new ArrayList<>();
        String isCompleteAnswer = "false";
        String isQuestionFulfilled = "false";

        String[] lines = responseText.split("\\r?\\n");
        boolean inFeedback = false;
        boolean inBlindKeywords = false;
        boolean expectingCompleteAnswer = false;
        boolean expectingQuestionFulfilled = false;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("피드백:")) {
                inFeedback = true;
                inBlindKeywords = false;
                expectingCompleteAnswer = false;
                expectingQuestionFulfilled = false;
                continue;
            }

            if (line.startsWith("블라인드 키워드:")) {
                inFeedback = false;
                inBlindKeywords = true;
                expectingCompleteAnswer = false;
                expectingQuestionFulfilled = false;
                continue;
            }

            if (line.startsWith("답변 완결 여부:")) {
                inFeedback = false;
                inBlindKeywords = false;
                expectingCompleteAnswer = true;
                continue;
            }

            if (line.startsWith("질문 충족도 판단:")) {
                inFeedback = false;
                inBlindKeywords = false;
                expectingQuestionFulfilled = true;
                continue;
            }

            if (expectingCompleteAnswer && line.startsWith("-")) {
                isCompleteAnswer = extractBooleanValue(line);
                expectingCompleteAnswer = false;
                continue;
            }

            if (expectingQuestionFulfilled && line.startsWith("-")) {
                isQuestionFulfilled = extractBooleanValue(line);
                expectingQuestionFulfilled = false;
                continue;
            }

            if (inFeedback && line.startsWith("-")) {
                feedbackLines.add(line.substring(1).trim());
            }

            if (inBlindKeywords && line.startsWith("-")) {
                String keyword = line.substring(1).trim();
                if (!keyword.equalsIgnoreCase("없음") && !keyword.isBlank()) {
                    blindKeywords.add(keyword);
                }
            }
        }

        String feedback = String.join("\n", feedbackLines);
        String blindKeywordStr = blindKeywords.isEmpty() ? "없음" : String.join(", ", blindKeywords);

        Map<String, String> result = new HashMap<>();
        result.put("feedback", feedback);
        result.put("blindKeywords", blindKeywordStr);
        result.put("isCompleteAnswer", isCompleteAnswer);
        result.put("isQuestionFulfilled", isQuestionFulfilled);

        return result;
    }

    private String extractBooleanValue(String line) {
        if (line.toLowerCase().contains("true")) {
            return "true";
        }
        if (line.toLowerCase().contains("false")) {
            return "false";
        }
        return "false";
    }


    private static final List<String> SECTION_TITLES = List.of(
            "강점", "약점", "AI 면접관 종합 평가", "이전 응시 대비 변화 및 비교 평가", "어휘 평가", "업무 성향 분석", "개발자 스타일 분석"
    );

    public Map<String, String> parseFeedback(String fullText) {
        log.info(fullText);
        Map<String, String> result = new LinkedHashMap<>();

        String titlePattern = SECTION_TITLES.stream()
                .sorted((a, b) -> b.length() - a.length())
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        String sectionRegex = "(?m)^\\s*(?:\\d+\\.\\s*)?(?:[-*]*\\s*)?\\*{0,3}\\s*(" + titlePattern + ")\\s*[:：]?\\s*\\*{0,3}\\s*$";
        Pattern pattern = Pattern.compile(sectionRegex);
        Matcher matcher = pattern.matcher(fullText);

        List<int[]> sectionPositions = new ArrayList<>();
        List<String> matchedTitles = new ArrayList<>();

        while (matcher.find()) {
            sectionPositions.add(new int[]{matcher.start(), matcher.end()});
            matchedTitles.add(matcher.group(1));
        }

        for (int i = 0; i < sectionPositions.size(); i++) {
            int startIdx = sectionPositions.get(i)[1];
            int endIdx = (i + 1 < sectionPositions.size()) ? sectionPositions.get(i + 1)[0] : fullText.length();

            String title = matchedTitles.get(i);
            String content = fullText.substring(startIdx, endIdx).strip();
            result.put(title, content);

            if ("어휘 평가".equals(title)) {
                result.putAll(parseVocabularySection(content));
            }
        }

        return result;
    }


    private Map<String, String> parseVocabularySection(String content) {
        Map<String, String> vocabMap = new LinkedHashMap<>();

        String[] lines = content.split("\\r?\\n");
        String repeatedWords = "";
        String levelComment = "";
        StringBuilder suggestions = new StringBuilder();

        boolean inSuggestionSection = false;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("- 반복 사용된 단어:")) {
                repeatedWords = line.replaceFirst("- 반복 사용된 단어:\\s*", "").trim();
            } else if (line.startsWith("- 어휘 수준 평가:")) {
                levelComment = line.replaceFirst("- 어휘 수준 평가:\\s*", "").trim();
            } else if (line.startsWith("- 개선 제안:")) {
                inSuggestionSection = true;
            } else if (inSuggestionSection && !line.isBlank()) {
                suggestions.append(line).append("\n");
            }
        }

        vocabMap.put("어휘 평가 - 반복 단어", repeatedWords);
        vocabMap.put("어휘 평가 - 수준 평가", levelComment);
        vocabMap.put("어휘 평가 - 개선 제안", suggestions.toString().trim());

        return vocabMap;
    }

}

