package org.nexusscode.backend.interview.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.service.AwsService;
import org.nexusscode.backend.interview.service.GeneratorService;
import org.nexusscode.backend.interview.service.delegation.InterviewQuestionService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class TtsProcessingEventListener {

    private final GeneratorService generatorService;
    private final AwsService awsService;
    private final InterviewQuestionService interviewQuestionService;

    @Async
    @EventListener
    public void handleTtsEvent(TtsProcessingEvent event) {
        InterviewQuestion question = event.getQuestion();
        GptVoice voice = event.getVoice();

        try {
            generatorService.generateQuestionVoiceAsync(question, voice)
                    .thenApplyAsync(voiceFile -> {
                        try {
                            String url = awsService.uploadTtsAudio(voiceFile);
                            question.saveTTSFileName(url);
                            interviewQuestionService.updateQuestion(question);
                            log.info("TTS 처리 완료 - questionId: {}, url: {}", question.getId(), url);
                        } catch (Exception e) {
                            log.error("TTS 업로드 실패 - questionId: {}", question.getId(), e);
                        }
                        return null;
                    }).exceptionally(ex -> {
                        log.error("TTS 처리 실패 - questionId: {}", question.getId(), ex);
                        return null;
                    });
        } catch (Exception e) {
            log.error("TTS 처리 중 예외 - questionId: {}", question.getId(), e);
        }
    }
}
