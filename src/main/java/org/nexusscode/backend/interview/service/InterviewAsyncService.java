package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.client.AwsClient;
import org.nexusscode.backend.interview.domain.*;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.nexusscode.backend.interview.service.delegation.AnswerFeedbackService;
import org.nexusscode.backend.interview.service.delegation.InterviewQuestionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSessionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSummaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class InterviewAsyncService {

    private final GeneratorService generatorService;

    private final InterviewQuestionService interviewQuestionService;
    private final InterviewSessionService interviewSessionService;
    private final InterviewSummaryService interviewSummaryService;
    private final AnswerFeedbackService answerFeedbackService;
    private final AwsService awsService;

    public CompletableFuture<Void> generateAdviceAsync(Long questionId, String audioUrl) {
        try {
            InterviewQuestion question = interviewQuestionService.findById(questionId)
                    .orElseThrow(() -> {
                        log.error("질문을 찾을 수 없습니다. questionId: {}", questionId);
                        return new CustomException(ErrorCode.QUESTION_NOT_FOUND);
                    });

            Map<String, Object> stringMap;
            try {
                stringMap = awsService.convertAudioText(audioUrl);
            } catch (Exception e) {
                log.error("STT 변환 실패 - questionId: {}, audioUrl: {}", questionId, audioUrl, e);
                return CompletableFuture.completedFuture(null);
            }

            InterviewAnswer answer = question.getAnswer();

            if (answer == null) {
                log.warn("questionId {} 에 대한 answer가 존재하지 않습니다", questionId);
                return CompletableFuture.completedFuture(null);
            }

            try {
                answer.saveScriptAndAudioLength(
                        (String) stringMap.get("transcript"),
                        (Integer) stringMap.get("duration")
                );

                Map<String, String> result = generatorService.generateAdviceFromInterviewQAndA(question);
                answerFeedbackService.createFeedback(answer, result.get("feedback"), result.get("blindKeywords"));
            } catch (Exception e) {
                log.error("답변 저장 또는 피드백 생성 중 오류 - questionId: {}", questionId, e);
                return CompletableFuture.completedFuture(null);
            }

            InterviewSession session = question.getSession();
            try {
                if (session.getQuestionCount() - 2 == question.getSeq() && !session.isAdditionalQuestion()) {
                    generateAdditionalQuestionsAsync(session.getId());
                } else if (session.getQuestionCount() - 1 == question.getSeq()) {
                    generateSummaryAsync(session);
                }
            } catch (Exception e) {
                log.error("추가 질문 생성 또는 요약 생성 중 오류 - sessionId: {}", session.getId(), e);
            }

        } catch (Exception e) {
            log.error("generateAdviceAsync 실행 중 예외 발생 - questionId: {}", questionId, e);
        }

        return CompletableFuture.completedFuture(null);
    }

    public void generateAdditionalQuestionsAsync(Long sessionId) {
        try {
            InterviewSession session = interviewSessionService.findById(sessionId)
                    .orElseThrow(() -> {
                        log.error("세션을 찾을 수 없습니다. sessionId: {}", sessionId);
                        return new CustomException(ErrorCode.NOT_FOUND);
                    });

            int count = ThreadLocalRandom.current().nextInt(1, 2);

            List<InterviewSessionDetailDto> details = interviewSessionService.getSessionDetails(session.getId())
                    .orElseThrow(() -> {
                        log.error("세션 상세 정보를 찾을 수 없습니다. sessionId: {}", session.getId());
                        return new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                    });

            List<InterviewQuestion> newQuestions = generatorService.generateQuestionsFromInterviewResponses(
                    details, count, session.getQuestions().size()
            );

            if (newQuestions == null || newQuestions.isEmpty()) {
                log.error("새로운 질문 생성중에 오류가 발생했습니다. sessionId: {}", sessionId);
                return;
            }

            try {
                Flux.fromIterable(newQuestions)
                        .flatMap(question ->
                                generatorService.generateQuestionVoiceAsync(question, session.getVoice())
                                        .map(voiceFile -> {
                                            try {
                                                String url = awsService.uploadTtsAudio(voiceFile);

                                                question.saveTTSUrl(url);

                                                return true;
                                            } catch (Exception e) {
                                                log.error("TTS 업로드 실패 - questionId: {}", question.getId(), e);
                                                return false;
                                            }
                                        })
                                        .onErrorResume(e -> {
                                            log.error("TTS 생성 실패 - questionId: {}", question.getId(), e);
                                            return Mono.empty();
                                        })
                        )
                        .collectList()
                        .block();
            } catch (Exception e) {
                log.error("TTS Flux 처리 중 예외 발생", e);
            }

            try {
                session.addQuestion(newQuestions);
            } catch (Exception e) {
                log.error("세션에 질문 추가 실패 - sessionId: {}", session.getId(), e);
                throw new CustomException(ErrorCode.QUESTION_SAVE_FAILED);
            }

        } catch (Exception e) {
            log.error("generateAdditionalQuestionsAsync 실행 중 예외 발생 - sessionId: {}", sessionId, e);
        }
    }

    public void generateSummaryAsync(InterviewSession session) {
        try {
            List<InterviewAdviceDTO> details = interviewSessionService.getInterviewAdvice(session.getId())
                    .orElseThrow(() -> {
                        log.error("세션 요약에 필요한 조언 데이터를 찾을 수 없습니다. sessionId: {}", session.getId());
                        return new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                    });

            String context = generatorService.generateSummaryFromInterviewAdvice(details);

            interviewSummaryService.createSummary(session, context);

        } catch (Exception e) {
            log.error("generateSummaryAsync 실행 중 예외 발생 - sessionId: {}", session.getId(), e);
        }
    }
}
