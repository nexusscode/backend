package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.client.AwsClient;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.service.delegation.InterviewAnswerService;
import org.nexusscode.backend.interview.service.delegation.InterviewQuestionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSessionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSummaryService;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class InterviewServiceImpl implements InterviewService {

    private final ResumeRepository resumeRepository;
    private final ResumeItemRepository resumeItemRepository;

    private final AwsClient awsClient;
    private final GeneratorService generatorService;
    private final AwsService awsService;

    private final InterviewCacheService interviewCacheService;
    private final InterviewSessionService interviewSessionService;
    private final InterviewQuestionService interviewQuestionService;
    private final InterviewAnswerService interviewAnswerService;
    private final InterviewSummaryService interviewSummaryService;
    private final InterviewAsyncFacadeService interviewAsyncFacadeService;

    @Override
    @Transactional
    public Long startInterview(InterviewStartRequest request) {
        //service로 교체
        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        List<ResumeItem> items = resumeItemRepository.findByResumeId(request.getResumeId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_ITEM_NOT_FOUND));
        //

        List<InterviewQuestion> questions = generatorService.generateQuestionsFromResume(items, 3);

        InterviewQuestion firstQuestion = questions.get(0);

        byte[] voiceFile = generatorService.generateQuestionVoiceSync(firstQuestion, request.getInterviewType());

        String fileUrl = awsService.uploadTtsAudio(voiceFile);

        firstQuestion.saveTTSUrl(fileUrl);

        InterviewSession session = interviewSessionService.createSession(request.getTitle(), questions, resume.getApplication(), request.getInterviewType());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                interviewCacheService.cacheQuestionsAsync(session);

                List<InterviewQuestion> remainingQuestions = questions.subList(1, questions.size());

                Flux.fromIterable(remainingQuestions)
                        .flatMap(question ->
                                generatorService.generateQuestionVoiceAsync(question, request.getInterviewType())
                                        .map(voiceFile -> {
                                            String url = awsService.uploadTtsAudio(voiceFile);

                                            question.saveTTSUrl(url);

                                            interviewQuestionService.updateQuestion(question);

                                            return true;
                                        })
                                        .onErrorResume(e -> {
                                            log.error("TTS 처리 실패: {}", question.getId(), e);
                                            return Mono.empty();
                                        })
                        )
                        .collectList()
                        .block();
            }
        });

        return session.getId();
    }

    @Override
    public QuestionAndHintDTO getQuestion(Long sessionId, Integer seq) {
        InterviewQuestion question = interviewCacheService.getCachedQuestionBySeq(sessionId, seq).orElseGet(() -> {
            InterviewQuestion result = interviewQuestionService.findQuestionAndHint(sessionId, seq)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            InterviewSession session = interviewSessionService.findById(sessionId).orElseThrow(() -> new CustomException(ErrorCode.SESSION_LIST_EMPTY));

            interviewCacheService.cacheQuestionsAsync(session);

            return result;
        });

        return QuestionAndHintDTO.builder()
                .interviewQuestionId(question.getId())
                .questionText(question.getQuestionText())
                .intentText(question.getIntentText())
                .seq(question.getSeq())
                .type(question.getInterviewType())
                .build();
    }

    @Override
    @Transactional
    public Long submitAnswer(InterviewAnswerRequest request) {
        InterviewQuestion question = interviewQuestionService.findById(request.getQuestionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long answerId = interviewAnswerService.saveAnswer(request, question);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                interviewAsyncFacadeService.generateAdviceAsyncFacade(request.getQuestionId(), request.getAudioUrl());
            }
        });

        return answerId;
    }

    @Override
    public List<InterviewSessionDTO> getList(Long applicationId) {
        return interviewSessionService
                .findSessionList(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_LIST_EMPTY));
    }

    @Override
    @Cacheable(value = "interviewFullDetailCache", key = "#sessionId")
    public InterviewAllSessionDTO getFullSessionDetail(Long sessionId) {
        InterviewSession session = interviewSessionService.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<InterviewQnADTO> questions = interviewSessionService.findInterviewQnA(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));

        String summary = interviewSummaryService.findBySessionId(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUMMARY_NOT_FOUND));

        return InterviewAllSessionDTO.createAllSessionDTO(session, questions, summary);
    }


    @Override
    @Transactional
    public boolean saveSessionToArchive(Long sessionId) {
        return interviewSessionService.saveSessionToArchive(sessionId);
    }

    @Override
    @Transactional
    public boolean deleteSessionToArchive(Long sessionId) {
        return interviewSessionService.deleteSessionFromArchive(sessionId);
    }

    @Override
    public String getPreSignUrl(String fileName) {
        return awsClient.generatePresignedUrl(fileName, Duration.ofSeconds(90));
    }
}

