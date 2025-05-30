package org.nexusscode.backend.resume.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeFeedbackStatus;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.domain.ResumeItemFeedback;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.repository.ResumeItemFeedbackRepository;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeItemService {

    private final UserService userService;
    private final ResumeService resumeService;
    private final ResumeItemRepository resumeItemRepository;
    private final ResumeItemFeedbackService resumeItemFeedbackService;
    private final ResumeItemFeedbackRepository resumeItemFeedbackRepository;
    private final ResumeFeedbackLimiterService resumeFeedbackLimiterService;

    @Transactional
    public List<ResumeItemResponseDto> createResumeItems(Long userId,Long resumeId,
        List<ResumeItemRequestDto> resumeItemRequestDtos) {
        User user = userService.findById(userId);
        Resume resume = resumeService.findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }

        List<ResumeItem> resumeItems = new ArrayList<>();

        for (ResumeItemRequestDto resumeItemRequestDto : resumeItemRequestDtos) {
            resumeFeedbackLimiterService.checkLimit(user.getId());
            ResumeItem resumeItem = ResumeItem.builder()
                .resume(resume)
                .question(resumeItemRequestDto.getQuestion())
                .answer(resumeItemRequestDto.getAnswer())
                .build();
            resumeItems.add(resumeItem);
            resume.addResumeItem(resumeItem);
            resumeItemRepository.save(resumeItem);

            String feedbackText = resumeItemFeedbackService.createResumeFeedback(
                resume.getApplication(), resumeItemRequestDto.getQuestion(),
                resumeItemRequestDto.getAnswer());
            ResumeItemFeedback feedback = ResumeItemFeedback.builder()
                .resumeItem(resumeItem)
                .feedbackText(feedbackText)
                .build();
            resumeItemFeedbackRepository.save(feedback);
            resume.updateAiCount();
        }
        resume.updateFeedbackStatus();
        resumeService.save(resume);
        return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
    }

    @Transactional
    public ResumeItemResponseDto createResumeItem(Long userId, Long resumeId, ResumeItemRequestDto resumeItemRequestDto) {
        User user = userService.findById(userId);
        Resume resume = resumeService.findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        resumeFeedbackLimiterService.checkLimit(user.getId());

        ResumeItem resumeItem = ResumeItem.builder()
            .resume(resume)
            .question(resumeItemRequestDto.getQuestion())
            .answer(resumeItemRequestDto.getAnswer())
            .build();
        resume.addResumeItem(resumeItem);
        resumeItemRepository.save(resumeItem);

        String feedbackText = resumeItemFeedbackService.createResumeFeedback(
            resume.getApplication(), resumeItemRequestDto.getQuestion(),
            resumeItemRequestDto.getAnswer());
        ResumeItemFeedback feedback = ResumeItemFeedback.builder()
            .resumeItem(resumeItem)
            .feedbackText(feedbackText)
            .build();
        resumeItemFeedbackRepository.save(feedback);
        resume.updateAiCount();
        resumeService.save(resume);

        return new ResumeItemResponseDto(resumeItem);
    }

    public List<ResumeItemResponseDto> getResumeItems(Long userId,Long resumeId) {
        User user = userService.findById(userId);
        Resume resume = resumeService.findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        List<ResumeItem> resumeItems = resumeItemRepository.findByResumeId(resume.getId())
            .orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_RESUME_ITEM)
            );

        return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
    }

    @Transactional
    public ResumeItemResponseDto updateResumeItem(Long userId, Long resumeItemId,
        ResumeItemRequestDto resumeItemRequestDto) {
        User user = userService.findById(userId);
        ResumeItem resumeItem = findById(resumeItemId);
        if(resumeItem.getResume().getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }

        resumeItem.updateResumeItem(resumeItemRequestDto);
        resumeItemRepository.save(resumeItem);

        resumeItem.getResume().touch();
        resumeService.save(resumeItem.getResume());

        return new ResumeItemResponseDto(resumeItem);
    }

    @Transactional
    public void deleteResumeItem(Long userId, Long resumeItemId) {
        User user = userService.findById(userId);
        ResumeItem resumeItem = findById(resumeItemId);
        if(resumeItem.getResume().getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }

        resumeItemRepository.delete(resumeItem);
    }

    @Transactional
    public List<ResumeItemResponseDto> uploadResumeItemsFromFile(Long userId, Long resumeId, MultipartFile file) {
        User user = userService.findById(userId);
        Resume resume = resumeService.findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();

            // 문항별로 분리 (1), 2., ③ 등 다양한 형식 대응
            String[] sections = text.split("(?=\\n?\\s*(\\d+[.)]|[①-⑩]))");

            List<ResumeItem> resumeItems = new ArrayList<>();

            for (String section : sections) {
                section = section.trim();
                if (section.isEmpty()) {
                    continue;
                }

                // 줄 나누기
                String[] lines = section.split("\\R+", 2);
                String question = lines[0].replaceAll("^(\\d+[.)]|[①-⑩])\\s*", "").trim();  // 번호 제거
                String answer = (lines.length > 1) ? lines[1].trim() : "";

                ResumeItem resumeItem = ResumeItem.builder()
                    .resume(resume)
                    .question(question)
                    .answer(answer)
                    .build();
                resumeItems.add(resumeItem);
                resume.addResumeItem(resumeItem);
                resumeItemRepository.save(resumeItem);

                String feedbackText = resumeItemFeedbackService.createResumeFeedback(
                    resume.getApplication(), question, answer);
                ResumeItemFeedback feedback = ResumeItemFeedback.builder()
                    .resumeItem(resumeItem)
                    .feedbackText(feedbackText)
                    .build();
                resumeItemFeedbackRepository.save(feedback);
                resume.updateAiCount();
                resumeService.save(resume);
            }
            resume.updateFeedbackStatus();
            resumeService.save(resume);

            return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
        } catch (Exception e) {
            System.out.println("uploading file fails : " + e.getMessage());
            throw new CustomException(ErrorCode.UPLOAD_RESUME_FAILURE);
        }
    }


    public ResumeItem findById(Long id) {
        return resumeItemRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_RESUME_ITEM)
        );
    }

    public Optional<List<ResumeItem>> findByResumeId(Long resumeId) {
        return resumeItemRepository.findByResumeId(resumeId);
    }
}
