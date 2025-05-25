package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.CustomException.EmptyGPTResponseException;
import org.nexusscode.backend.interview.client.GPTClient;
import org.nexusscode.backend.interview.client.OpenAITTSClient;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewType;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.nexusscode.backend.interview.dto.InterviewSummaryDTO;
import org.nexusscode.backend.interview.service.support.PromptType;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.nexusscode.backend.interview.service.support.InterviewServiceUtil.*;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class GeneratorService {

    private final GPTClient gptClient;
    private final OpenAITTSClient openAITTSClient;

    @Retryable(
            value = { EmptyGPTResponseException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<InterviewQuestion> generateQuestionsFromResume(List<ResumeItem> items, int count) {
        String resumeText = makeTextFromResumeItems(items);

        try {
            List<InterviewQuestionDTO> questions = gptClient.generateInterviewQuestions(resumeText, buildPrompt(PromptType.RESUME, count));

            if (questions.isEmpty()) {
                throw new EmptyGPTResponseException("GPT returned empty questions");
            }

            return mapToInterviewQuestions(questions, 0, count);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    @Recover
    public List<InterviewQuestion> recover(EmptyGPTResponseException e, List<ResumeItem> items, int count) {
        log.error("GPT 질문 생성 실패 - 기본 질문 반환 or null", e);
        throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
    }

    @Retryable(
            value = { EmptyGPTResponseException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<InterviewQuestion> generateQuestionsFromInterviewResponses(List<InterviewSessionDetailDto> responses, int count, int startSeq) {
        String text = makeTextFromInterviewDetail(responses);

        try {
            List<InterviewQuestionDTO> questions = gptClient.generateInterviewQuestions(text, buildPrompt(PromptType.INTERVIEW, count));

            if (questions.isEmpty()) {
                throw new EmptyGPTResponseException("GPT returned empty questions");
            }

            return mapToInterviewQuestions(questions, startSeq, count);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    @Recover
    public List<InterviewQuestion> recover(EmptyGPTResponseException e, List<InterviewSessionDetailDto> responses, int count, int startSeq) {
        log.error("GPT 질문 생성 실패 - 기본 질문 반환 or null", e);
        throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
    }

    @Retryable(
            value = { EmptyGPTResponseException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public InterviewSummaryDTO generateSummaryFromInterviewAdvice(List<InterviewAdviceDTO> responses, List<InterviewQuestion> questions) {
        String text = makeTextFromInterviewAdvice(responses);
        StringBuilder builder = new StringBuilder();
        if (questions == null) {
            builder.append("이번이 첫 번째 응시입니다.");
        } else {
            for (InterviewQuestion question : questions) {
                builder.append(makeTextFromInterviewQAndA(question));
            }
        }

        try {
            Map<String, String> summayMap = gptClient.generateSummary(text, builder.toString().trim(),  buildPrompt(PromptType.SUMMARY, 0));
            log.info(summayMap);

            if (summayMap == null || summayMap.isEmpty()) {
                throw new EmptyGPTResponseException("GPT returned empty questions");
            }

            return InterviewSummaryDTO.builder()
                    .strengths(summayMap.getOrDefault("강점", ""))
                    .weaknesses(summayMap.getOrDefault("약점", ""))
                    .overallAssessment(summayMap.getOrDefault("AI 면접관 종합 평가", ""))
                    .comparisonWithPrevious(summayMap.getOrDefault("이전 응시 대비 변화 및 비교 평가", ""))
                    .vocabularyRepeatedWords(summayMap.getOrDefault("어휘 평가 - 반복 단어", ""))
                    .vocabularyLevelComment(summayMap.getOrDefault("어휘 평가 - 수준 평가", ""))
                    .vocabularySuggestions(summayMap.getOrDefault("어휘 평가 - 개선 제안", ""))
                    .workAttitude(summayMap.getOrDefault("업무 성향 분석", ""))
                    .developerStyle(summayMap.getOrDefault("개발자 스타일 분석", ""))
                    .build();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    @Recover
    public List<InterviewQuestion> recover(EmptyGPTResponseException e, List<InterviewAdviceDTO> responses) {
        log.error("GPT 질문 생성 실패 - 기본 질문 반환 or null", e);
        throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
    }

    @Retryable(
            value = { EmptyGPTResponseException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public Map<String, String> generateAdviceFromInterviewQAndA(InterviewQuestion question) {
        String text = makeTextFromInterviewQAndA(question);
        log.info(text);

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
                throw new EmptyGPTResponseException("GPT returned empty questions");
            }

            return result;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
        }
    }

    @Recover
    public List<InterviewQuestion> recover(EmptyGPTResponseException e, InterviewQuestion question) {
        log.error("GPT 질문 생성 실패 - 기본 질문 반환 or null", e);
        throw new CustomException(ErrorCode.GPT_RESPONSE_ERROR);
    }

    public CompletableFuture<byte[]> generateQuestionVoiceAsync(InterviewQuestion question, GptVoice voiceType) {
        return openAITTSClient.textToSpeechAsync(question.getQuestionText(), voiceType);
    }

    public byte[] generateQuestionVoiceSync(InterviewQuestion question, GptVoice voiceType) {
        try {
            return openAITTSClient.textToSpeechSync(question.getQuestionText(), voiceType);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TTS_FAILED);
        }
    }
}
