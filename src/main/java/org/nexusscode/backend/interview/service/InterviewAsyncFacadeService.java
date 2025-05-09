package org.nexusscode.backend.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class InterviewAsyncFacadeService {

    private final InterviewAsyncService interviewAsyncService;

    @Async
    public void generateAdviceAsyncFacade(Long questionId, String audioUrl) {
        interviewAsyncService.generateAdviceAsync(questionId, audioUrl);
    }

}
