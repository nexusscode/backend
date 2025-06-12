package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.*;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.nexusscode.backend.interview.dto.InterviewSummaryDTO;
import org.nexusscode.backend.interview.event.InterviewSummaryNotifier;
import org.nexusscode.backend.interview.service.delegation.AnswerFeedbackService;
import org.nexusscode.backend.interview.service.delegation.InterviewQuestionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSessionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSummaryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

    private final InterviewSummaryNotifier summaryNotifier;

    public CompletableFuture<Void> generateAdviceAsync(Long questionId, String audioUrl) {
        try {
            InterviewQuestion question = findValidQuestion(questionId);
            Map<String, Object> sttResult = convertAudioToText(audioUrl, questionId);
            if (sttResult == null) return CompletableFuture.completedFuture(null);

            InterviewAnswer answer = saveScriptAndFeedback(question, sttResult);
            if (answer == null) return CompletableFuture.completedFuture(null);

            handleSessionPostProcessing(question);

        } catch (Exception e) {
            log.error("generateAdviceAsync 실행 중 예외 발생 - questionId: {}", questionId, e);
        }

        return CompletableFuture.completedFuture(null);
    }


    private InterviewQuestion findValidQuestion(Long questionId) {
        return interviewQuestionService.findById(questionId)
                .orElseThrow(() -> {
                    log.error("질문을 찾을 수 없습니다. questionId: {}", questionId);
                    return new CustomException(ErrorCode.QUESTION_NOT_FOUND);
                });
    }

    private Map<String, Object> convertAudioToText(String audioUrl, Long questionId) {
        try {
            return awsService.convertAudioText(audioUrl);
        } catch (Exception e) {
            log.error("STT 변환 실패 - questionId: {}, audioUrl: {}", questionId, audioUrl, e);
            return null;
        }
    }

    private InterviewAnswer saveScriptAndFeedback(InterviewQuestion question, Map<String, Object> stringMap) {
        InterviewAnswer answer = question.getAnswer();
        if (answer == null || answer.getAnswerStatus() == AnswerStatus.PASS) {
            log.warn("questionId {} 에 대한 answer가 존재하지 않습니다", question.getId());
            return null;
        }else if (answer.getAudioLength() == 0 || answer.getTranscript() == null || answer.getTranscript().length() == 0) {
            answer.changeAnswerStatus(AnswerStatus.FAILED);
            return null;
        }

        try {
            answer.saveScriptAndAudioLengthAndStatus(
                    (String) stringMap.get("transcript"),
                    (Integer) stringMap.get("duration"),
                    AnswerStatus.DONE
            );

            Map<String, String> result = generatorService.generateAdviceFromInterviewQAndA(question);
            log.info(result.toString());

            answerFeedbackService.createFeedback(
                    answer,
                    result.get("feedback"),
                    result.get("blindKeywords"),
                    Boolean.parseBoolean(result.get("isCompleteAnswer")),
                    Boolean.parseBoolean(result.get("isQuestionFulfilled"))
            );
        } catch (Exception e) {
            log.error("답변 저장 또는 피드백 생성 중 오류 - questionId: {}", question.getId(), e);
            return null;
        }

        return answer;
    }

    private void handleSessionPostProcessing(InterviewQuestion question) {
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
    }

    public void generateAdditionalQuestionsAsync(Long sessionId) {
        try {
            InterviewSession session = getValidSession(sessionId);
            List<InterviewSessionDetailDto> details = getSessionDetails(session);
            List<InterviewQuestion> newQuestions = generateNewQuestions(details, session);

            if (newQuestions == null || newQuestions.isEmpty()) {
                log.error("새로운 질문 생성 중 오류 발생 - sessionId: {}", sessionId);
                return;
            }

            processTTSForQuestions(newQuestions, session);
            addQuestionsToSession(session, newQuestions);

        } catch (Exception e) {
            log.error("generateAdditionalQuestionsAsync 실행 중 예외 발생 - sessionId: {}", sessionId, e);
        }
    }


    private InterviewSession getValidSession(Long sessionId) {
        return interviewSessionService.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("세션을 찾을 수 없습니다. sessionId: {}", sessionId);
                    return new CustomException(ErrorCode.NOT_FOUND);
                });
    }

    private List<InterviewSessionDetailDto> getSessionDetails(InterviewSession session) {
        return interviewSessionService.getSessionDetails(session.getId())
                .orElseThrow(() -> {
                    log.error("세션 상세 정보를 찾을 수 없습니다. sessionId: {}", session.getId());
                    return new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                });
    }

    private List<InterviewQuestion> generateNewQuestions(List<InterviewSessionDetailDto> details, InterviewSession session) {
        int count = ThreadLocalRandom.current().nextInt(1, 2);
        return generatorService.generateQuestionsFromInterviewResponses(
                details, count, session.getQuestions().size()
        );
    }

    private void processTTSForQuestions(List<InterviewQuestion> newQuestions, InterviewSession session) {
        List<CompletableFuture<Boolean>> futures = newQuestions.stream()
                .map(question -> generatorService.generateQuestionVoiceAsync(question, session.getVoice())
                        .thenApplyAsync(voiceFile -> {
                            try {
                                String url = awsService.uploadTtsAudio(voiceFile);
                                question.saveTTSFileName(url);
                                return true;
                            } catch (Exception e) {
                                log.error("TTS 업로드 실패 - questionId: {}", question.getId(), e);
                                return false;
                            }
                        }).exceptionally(ex -> {
                            log.error("TTS 생성 실패 - questionId: {}", question.getId(), ex);
                            return false;
                        })
                )
                .collect(Collectors.toList());

        // 모든 작업이 완료될 때까지 기다림
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allDone.get();
        } catch (Exception e) {
            log.error("TTS CompletableFuture 처리 중 예외 발생", e);
        }
    }

    private void addQuestionsToSession(InterviewSession session, List<InterviewQuestion> newQuestions) {
        try {
            session.addQuestion(newQuestions);
        } catch (Exception e) {
            log.error("세션에 질문 추가 실패 - sessionId: {}", session.getId(), e);
            throw new CustomException(ErrorCode.QUESTION_SAVE_FAILED);
        }
    }


    public void generateSummaryAsync(InterviewSession session) {
        try {
            List<InterviewAdviceDTO> adviceDetails = getAdviceDetails(session);
            List<InterviewQuestion> previousQuestions = getPreviousSessionQuestions(session);

            InterviewSummaryDTO summary = generatorService.generateSummaryFromInterviewAdvice(adviceDetails, previousQuestions);

            interviewSummaryService.createSummary(session, summary);

            summaryNotifier.notifySummaryComplete(session.getId());

        } catch (Exception e) {
            log.error("generateSummaryAsync 실행 중 예외 발생 - sessionId: {}", session.getId(), e);
        }
    }

    private List<InterviewAdviceDTO> getAdviceDetails(InterviewSession session) {
        return interviewSessionService.getInterviewAdvice(session.getId())
                .orElseThrow(() -> {
                    log.error("세션 요약에 필요한 조언 데이터를 찾을 수 없습니다. sessionId: {}", session.getId());
                    return new CustomException(ErrorCode.INVALID_INPUT_VALUE);
                });
    }

    private List<InterviewQuestion> getPreviousSessionQuestions(InterviewSession session) {
        InterviewSession previousSession = interviewSessionService
                .findByApplicationId(session.getApplication().getId())
                .filter(list -> list.size() > 1)
                .map(list -> list.get(1))
                .orElse(null);

        return previousSession != null ? previousSession.getQuestions() : null;
    }



}
