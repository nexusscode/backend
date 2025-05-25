package org.nexusscode.backend.interview.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nexusscode.backend.interview.service.InterviewAsyncService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdviceEventListener {

    private final InterviewAsyncService interviewAsyncService;

    @Async
    @EventListener
    public void handleAdviceEvent(AdviceGenerationEvent event) {
        try {
            log.info("이벤트 수신 - 비동기 처리 시작: questionId={}, audioUrl={}", event.getQuestionId(), event.getAudioUrl());
            interviewAsyncService.generateAdviceAsync(event.getQuestionId(), event.getAudioUrl());
        } catch (Exception e) {
            log.error("Advice 비동기 처리 중 예외", e);
        }
    }
}
