package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.InterviewAnswer;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.dto.InterviewAnswerRequest;
import org.nexusscode.backend.interview.repository.InterviewAnswerRepository;
import org.nexusscode.backend.interview.repository.InterviewQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterviewAnswerService {
    private final InterviewAnswerRepository interviewAnswerRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    public Long saveAnswer(InterviewAnswerRequest request, InterviewQuestion question) {
        InterviewAnswer answer = InterviewAnswer.createInterviewAnswer(request, question);

        try {
            return interviewAnswerRepository.save(answer).getId();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.ANSWER_SAVE_FAILED);
        }
    }

    public void updateTranscriptAndAudioLength(Long answerId, String transcript, int audioLength) {
        InterviewAnswer answer = interviewAnswerRepository.findById(answerId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        answer.saveScriptAndAudioLength(transcript, audioLength);
    }

    public Optional<InterviewAnswer> findById(Long answerId) {
        return interviewAnswerRepository.findById(answerId);
    }

    public void deleteById(Long answerId) {
        interviewAnswerRepository.deleteById(answerId);
    }
}
