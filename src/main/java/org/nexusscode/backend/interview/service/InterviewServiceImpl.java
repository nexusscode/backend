package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.global.aop.limit.RateLimit;
import org.nexusscode.backend.global.aop.lock.RedissonLock;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.client.AwsClient;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.event.AdviceGenerationEvent;
import org.nexusscode.backend.interview.event.TtsProcessingEvent;
import org.nexusscode.backend.interview.service.delegation.InterviewAnswerService;
import org.nexusscode.backend.interview.service.delegation.InterviewQuestionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSessionService;
import org.nexusscode.backend.interview.service.delegation.InterviewSummaryService;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.service.ResumeService;
import org.nexusscode.backend.user.dto.UserDTO;
import org.nexusscode.backend.user.service.UserStatService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class InterviewServiceImpl implements InterviewService {

    private final ResumeService resumeService;
    private final UserStatService userStatService;

    private final AwsClient awsClient;
    private final GeneratorService generatorService;
    private final AwsService awsService;

    private final InterviewCacheService interviewCacheService;
    private final InterviewSessionService interviewSessionService;
    private final InterviewQuestionService interviewQuestionService;
    private final InterviewAnswerService interviewAnswerService;
    private final InterviewSummaryService interviewSummaryService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    @RateLimit(limit = 5, duration = 24, timeUnit = ChronoUnit.HOURS)
    @RedissonLock(key = "'start:' + #userId + ':' + #request.applicationId")
    public Long startInterview(InterviewStartRequest request, Long userId) {
        Resume resume = resumeService.findResumeListByApplicationId(request.getApplicationId());
        List<InterviewQuestion> questions = generateQuestionsFromResume(resume);

        JobApplication application = resume.getApplication();
        InterviewSession session = interviewSessionService.createSession(
                application.getCompanyName() + " " + application.getJobTitle() + "면접",
                questions,
                application,
                application.getUser().getId(),
                request.getInterviewType()
        );

        processFirstQuestionTTS(questions.get(0), request);

        registerPostCommitAsyncProcessing(session, questions.subList(1, questions.size()), request, userId);

        userStatService.getUserStatAndIncrement(userId);

        return session.getId();
    }

    private List<InterviewQuestion> generateQuestionsFromResume(Resume resume) {
        List<ResumeItem> items = resume.getResumeItems();
        return generatorService.generateQuestionsFromResume(items, 3);
    }

    private void processFirstQuestionTTS(InterviewQuestion firstQuestion, InterviewStartRequest request) {
        byte[] voiceFile = generatorService.generateQuestionVoiceSync(firstQuestion, request.getInterviewType());
        String fileName = awsService.uploadTtsAudio(voiceFile);
        firstQuestion.saveTTSFileName(fileName);
    }

    private void registerPostCommitAsyncProcessing(InterviewSession session, List<InterviewQuestion> remainingQuestions, InterviewStartRequest request, Long userId) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                interviewCacheService.cacheQuestionsAsync(session);
                processRemainingQuestionsTTS(remainingQuestions, request);
            }
        });
    }

    private void processRemainingQuestionsTTS(List<InterviewQuestion> questions, InterviewStartRequest request) {
        questions.forEach(q -> {
            applicationEventPublisher.publishEvent(new TtsProcessingEvent(q, request.getInterviewType()));
        });
    }


    @Override
    public QuestionAndHintDTO getQuestion(Long sessionId, Integer seq) {
        InterviewQuestion question = interviewCacheService.getCachedQuestionBySeq(sessionId, seq).orElseGet(() -> {
            InterviewQuestion result = interviewQuestionService.findQuestionAndHint(sessionId, seq)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            InterviewSession session = interviewSessionService.findById(sessionId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SESSION_LIST_EMPTY));

            interviewCacheService.cacheQuestionsAsync(session);

            return result;
        });

        InterviewSession session = interviewSessionService.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_LIST_EMPTY));

        return QuestionAndHintDTO.builder()
                .interviewQuestionId(question.getId())
                .questionText(question.getQuestionText())
                .intentText(question.getIntentText())
                .seq(question.getSeq())
                .type(question.getInterviewType())
                .ttsUrl(getAIVoicePreSignUrl(question.getTtsFileName()))
                .videoUrl(changeVoiceTypeToVideoUrl(session.getVoice()))
                .countAll(session.getQuestionCount())
                .build();
    }

    private String changeVoiceTypeToVideoUrl(GptVoice voice) {
        if (voice == GptVoice.ONYX) {
            return getAIVideoPreSignUrl("onyx_video.mp4");
        } else if (voice == GptVoice.ALLOY) {
            return getAIVideoPreSignUrl("alloy_video.mp4");
        } else if (voice == GptVoice.NOVA) {
            return getAIVideoPreSignUrl("nova_video.mp4");
        }else if (voice == GptVoice.ECHO) {
            return getAIVideoPreSignUrl("echo_video.mp4");
        }
        throw new CustomException(ErrorCode.TYPE_NOT_FOUND);
    }

    @Override
    @Transactional
    @RedissonLock(key = "'answer:' + #userId + ':' + #request.questionId")
    public Long submitAnswer(InterviewAnswerRequest request, Long userId) {
        InterviewQuestion question = interviewQuestionService.findById(request.getQuestionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long answerId = interviewAnswerService.saveAnswer(request, question);

        processGenerateAdvice(request);

        return answerId;
    }

    private void processGenerateAdvice(InterviewAnswerRequest request) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                applicationEventPublisher.publishEvent(new AdviceGenerationEvent(request.getQuestionId(), request.getAudioUrl()));
            }
        });
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

        InterviewSummary summary = interviewSummaryService.findBySessionId(sessionId)
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
    public String getUserVoicePreSignUrl(String fileName) {
        return awsClient.generateUserVoicePresignedUrl(fileName, Duration.ofSeconds(600));
    }

    @Override
    public String getAIVoicePreSignUrl(String fileName) {
        return awsClient.generateAIVoicePresignedUrl(fileName, Duration.ofSeconds(600));
    }

    @Override
    public String getAIVideoPreSignUrl(String fileName) {
        return awsClient.generateAIVideoPresignedUrl(fileName, Duration.ofSeconds(600));
    }

    @Override
    public Boolean deleteSession(Long sessionId) {
        return interviewSessionService.deleteSession(sessionId);
    }

    @Override
    public InterviewRecentSessionDTO getRecentSession(Long applicationId) {
        InterviewSession session = interviewSessionService.findByApplicationId(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND)).get(0);

        List<InterviewQuestion> questions = session.getQuestions();

        for (InterviewQuestion question : questions) {
            if (question.getAnswer() == null) {
                return InterviewRecentSessionDTO.builder().sessionId(session.getId()).seq(question.getSeq()).build();
            }
        }
        throw new CustomException(ErrorCode.NOT_CONNECT_SESSION);
    }

    @Override
    public InterviewSessionDTO getRecentInterviewSessionByUserId(Long userId) {
        return interviewSessionService.findRecentSessionByUserId(userId);
    }

    @Override
    public Integer getInterviewCallCount(Long userId) {
        return userStatService.getUserStat(userId).getTotalInterviews();
    }

    @Override
    public Long passAnswer(Long questionId, Long userId) {
        InterviewQuestion question = interviewQuestionService.findById(questionId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return interviewAnswerService.saveAnswer(questionId, question);
    }

    @Override
    public RateLimitStatusDTO getInterviewRateLimitStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth.getPrincipal() instanceof UserDTO user)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String userEmail = user.getEmail();
        String key = "rate:startInterview:" + userEmail;

        Object raw = redisTemplate.opsForValue().get(key);
        long count = raw != null ? ((Number) raw).longValue() : 0L;
        count = Math.min(5, count);

        Long ttlSeconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        long remaining = Math.max(0, 5 - count);

        return new RateLimitStatusDTO(
                5,
                count,
                remaining,
                ttlSeconds != null && ttlSeconds > 0 ? ttlSeconds : 0
        );
    }

}

