package org.nexusscode.backend.interview.service.delegation;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.interview.domain.InterviewQnAStorageBox;
import org.nexusscode.backend.interview.domain.InterviewSummaryStorageBox;
import org.nexusscode.backend.interview.dto.InterviewAllSessionDTO;
import org.nexusscode.backend.interview.repository.InterviewStorageBoxRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewStorageBoxService {

    private final InterviewStorageBoxRepository boxRepository;
    private final UserService userService;

    @Transactional
    public Long create(Long userId, InterviewAllSessionDTO dto) {
        User user = userService.findById(userId);

        InterviewSummaryStorageBox storageBox = InterviewSummaryStorageBox.builder()
                .user(user)
                .sessionTitle(dto.getTitle())
                .totalQuestionCount(dto.getTotalCount())
                .countSeconds(dto.getCountSeconds())
                .strengths(dto.getStrengths())
                .weaknesses(dto.getWeaknesses())
                .overallAssessment(dto.getOverallAssessment())
                .comparisonWithPrevious(dto.getComparisonWithPrevious())
                .vocabularyEvaluation(dto.getVocabularyEvaluation())
                .workAttitude(dto.getWorkAttitude())
                .developerStyle(dto.getDeveloperStyle())
                .notCompleteAnswer(dto.getNotCompleteAnswer())
                .blindList(dto.getBlindList())
                .build();

        List<InterviewQnAStorageBox> qnaList = dto.getQuestions().stream()
                .map(q -> InterviewQnAStorageBox.builder()
                        .storageBox(storageBox)
                        .questionText(q.getQuestionText())
                        .transcript(q.getTranscript())
                        .feedback(q.getFeedback())
                        .second(q.getSecond())
                        .cheated(q.isCheated())
                        .completeAnswer(q.isCompleteAnswer())
                        .questionFulfilled(q.isQuestionFulfilled())
                        .blindKeywords(q.getBlindKeywords())
                        .build())
                .collect(Collectors.toList());

        storageBox.getQuestions().addAll(qnaList);

        boxRepository.save(storageBox);

        return storageBox.getId();
    }

    @Transactional(readOnly = true)
    public InterviewSummaryStorageBox get(Long storageBoxId, Long userId) {
        return boxRepository.findByIdAndUserId(storageBoxId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    @Transactional
    public boolean delete(Long storageBoxId, Long userId) {
        InterviewSummaryStorageBox box = boxRepository.findByIdAndUserId(storageBoxId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        boxRepository.delete(box);

        return true;
    }

    @Transactional(readOnly = true)
    public Page<InterviewSummaryStorageBox> list(Long userId, String searchWord, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (searchWord != null && !searchWord.isBlank()) {
            return boxRepository.findAllByUserIdAndKeyword(userId, searchWord, pageable);
        } else {
            return boxRepository.findAllByUserId(userId, pageable);
        }
    }
}
