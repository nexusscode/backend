package org.nexusscode.backend.interview.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class GPTClient {

    private final ChatClient chatClient;


    public List<InterviewQuestionDTO> generateInterviewQuestions(String inputText, PromptTemplate pro) {
        return parseQuestionsFrom(callClient(inputText, pro));
    }

    public String generateSummary(String inputText, PromptTemplate pro) {
        return callClient(inputText, pro);
    }

    public Map<String, String> generateAdvice(String inputText, PromptTemplate pro) {
        return parseGptAdviceResponse(callClient(inputText, pro));
    }

    private String callClient(String inputText, PromptTemplate pro) {
        try {
            PromptTemplate promptTemplate = pro;
            String prompt = promptTemplate.create(Map.of("inputText", inputText)).toString();

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
        List<String> feedbackLines = new ArrayList<>();
        List<String> blindKeywords = new ArrayList<>();

        String[] lines = responseText.split("\\r?\\n");
        boolean inFeedback = false;
        boolean inBlindKeywords = false;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("피드백:")) {
                inFeedback = true;
                inBlindKeywords = false;
                continue;
            }

            if (line.startsWith("블라인드 키워드:")) {
                inFeedback = false;
                inBlindKeywords = true;
                continue;
            }

            if (inFeedback && line.startsWith("-")) {
                feedbackLines.add(line.substring(1).trim());
            }

            if (inBlindKeywords && line.startsWith("-")) {
                blindKeywords.add(line.substring(1).trim());
            }
        }

        String feedback = String.join("\n", feedbackLines);
        String blindKeywordStr = String.join(", ", blindKeywords);

        Map<String, String> result = new HashMap<>();
        result.put("feedback", feedback);
        result.put("blindKeywords", blindKeywordStr);
        return result;
    }

}

