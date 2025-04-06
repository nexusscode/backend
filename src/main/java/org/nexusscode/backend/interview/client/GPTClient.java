package org.nexusscode.backend.interview.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class GPTClient {

    private final ChatClient chatClient;

    public List<InterviewQuestionDTO> generateInterviewQuestions(String resumeText) {
        PromptTemplate promptTemplate = buildPrompt();
        String prompt = promptTemplate.create(Map.of("resume", resumeText)).toString();

        String content = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        return parseQuestionsFrom(content);
    }

    private PromptTemplate buildPrompt() {
        return new PromptTemplate("""
            아래는 한 사람의 자기소개서입니다.

            이 내용을 바탕으로, 예상 면접 질문 5개와 각 질문의 의도를 알려주세요.

            형식 예:
            질문: ~~~
            의도: ~~~

            자기소개서:
            {resume}
        """);
    }

    private List<InterviewQuestionDTO> parseQuestionsFrom(String gptOutput) {
        List<InterviewQuestionDTO> result = new ArrayList<>();
        String[] blocks = gptOutput.split("(?=질문\\s*\\d*:)");

        for (String block : blocks) {
            String question = null;
            String intent = null;

            for (String line : block.strip().split("\\r?\\n")) {
                if (line.matches("^질문\\s*\\d*:\\s*.*")) {
                    question = line.replaceFirst("^질문\\s*\\d*:\\s*", "").trim();
                } else if (line.startsWith("의도:")) {
                    intent = line.replaceFirst("의도:", "").trim();
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

}

