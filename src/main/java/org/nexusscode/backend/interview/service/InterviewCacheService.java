package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.dto.InterviewAllSessionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.repository.InterviewQuestionRepository;
import org.nexusscode.backend.interview.repository.RedisRepository;
import org.nexusscode.backend.interview.service.delegation.InterviewQuestionService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewCacheService {

    private final RedisRepository redisRepository;
    private static final Duration QUESTION_TTL = Duration.ofHours(1);

    private String getQuestionMapKey(Long sessionId) {
        return "interview:session:" + sessionId + ":questions";
    }

    @Async
    public void cacheQuestionsAsync(InterviewSession session) {
        String key = getQuestionMapKey(session.getId());

        for (InterviewQuestion q : session.getQuestions()) {
            redisRepository.putHash(key, q.getSeq(), q);
        }

        redisRepository.expire(key, QUESTION_TTL);
    }

    public Optional<InterviewQuestion> getCachedQuestionBySeq(Long sessionId, int seq) {
        return redisRepository.getHash(getQuestionMapKey(sessionId), String.valueOf(seq), InterviewQuestion.class);
    }
}
