package org.nexusscode.backend.resume.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeFeedbackService {

    private final ChatClient chatClient;

    public String getResumeFeedback(String question, String answer) {
        PromptTemplate promptTemplate = new PromptTemplate("""
            아래는 자소서 문항과 답변입니다.

            1. 질문에 적절한 답변인지 평가해 주세요.
            2. 맞춤법 및 문장 오류가 있다면 알려주세요.
            3. 전반적인 개선점을 제안해 주세요.

            질문: {question}
            답변: {answer}
            """);

        String prompt = promptTemplate.create(Map.of(
            "question", question,
            "answer", answer
        )).toString();

        String content = chatClient
            .prompt()
            .user(prompt)
            .call()
            .content();

        return content;
    }
}
