package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.client.GPTClient;
import org.nexusscode.backend.interview.client.OpenAITTSClient;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewType;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.nexusscode.backend.interview.service.support.PromptType;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.nexusscode.backend.interview.service.support.InterviewServiceUtil.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GeneratorService {

    private final GPTClient gptClient;
    private final OpenAITTSClient openAITTSClient;

    public List<InterviewQuestion> generateQuestionsFromResume(List<ResumeItem> items, int count) {
        String resumeText = makeTextFromResumeItems(items);

        try {
            List<InterviewQuestionDTO> questions = gptClient.generateInterviewQuestions(resumeText, buildPrompt(PromptType.RESUME, count));

            if (questions.isEmpty()) {
                throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
            }

            return mapToInterviewQuestions(questions, 0, count);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    public List<InterviewQuestion> generateQuestionsFromInterviewResponses(List<InterviewSessionDetailDto> responses, int count, int startSeq) {
        String text = makeTextFromInterviewDetail(responses);

        try {
            List<InterviewQuestionDTO> questions = gptClient.generateInterviewQuestions(text, buildPrompt(PromptType.INTERVIEW, count));

            if (questions.isEmpty()) {
                throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
            }

            return mapToInterviewQuestions(questions, startSeq, count);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    public String generateSummaryFromInterviewAdvice(List<InterviewAdviceDTO> responses) {
        String text = makeTextFromInterviewAdvice(responses);

        try {
            String result = gptClient.generateSummary(text, buildPrompt(PromptType.SUMMARY, 0));

            if (result == null || result.isEmpty()) {
                throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
            }

            return result;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    public Map<String, String> generateAdviceFromInterviewQAndA(InterviewQuestion question) {
        String text = makeTextFromInterviewQAndA(question);

        Map<String, String> result = null;
        try {
            if (question.getInterviewType() == InterviewType.PERSONALITY) {
                result = gptClient.generateAdvice(text, buildPrompt(PromptType.ADVICE_PER, 0));
            } else if (question.getInterviewType() == InterviewType.TECHNICAL) {
                result =  gptClient.generateAdvice(text, buildPrompt(PromptType.ADVICE_TECH, 0));
            }else{
                throw new CustomException(ErrorCode.NOT_FOUND);
            }

            if (result == null || result.isEmpty()) {
                throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
            }

            return result;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    @Async
    public Mono<byte[]> generateQuestionVoiceAsync(InterviewQuestion question, GptVoice voiceType) {
        return openAITTSClient.textToSpeechAsync(question.getQuestionText(), voiceType)
                .onErrorMap(e -> new CustomException(ErrorCode.TTS_FAILED));
    }

    public byte[] generateQuestionVoiceSync(InterviewQuestion question, GptVoice voiceType) {
        try {
            return openAITTSClient.textToSpeechSync(question.getQuestionText(), voiceType);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TTS_FAILED);
        }
    }
}
