package org.nexusscode.backend.resume.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.domain.ResumeFeedback;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.repository.ResumeFeedbackRepository;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeItemService {

    private final ResumeService resumeService;
    private final ResumeItemRepository resumeItemRepository;
    private final ResumeFeedbackService resumeFeedbackService;
    private final ResumeFeedbackRepository resumeFeedbackRepository;

    @Transactional
    public List<ResumeItemResponseDto> createResumeItems(Long resumeId,
        List<ResumeItemRequestDto> resumeItemRequestDtos) {
        Resume resume = resumeService.findById(resumeId);
        List<ResumeItem> resumeItems = new ArrayList<>();

        for (ResumeItemRequestDto resumeItemRequestDto : resumeItemRequestDtos) {
            ResumeItem resumeItem = ResumeItem.builder()
                .resume(resume)
                .question(resumeItemRequestDto.getQuestion())
                .answer(resumeItemRequestDto.getAnswer())
                .build();
            resumeItems.add(resumeItem);
            resume.addResumeItem(resumeItem);
            resumeItemRepository.save(resumeItem);

            String feedbackText = resumeFeedbackService.createResumeFeedback(resume.getApplication(),resumeItemRequestDto.getQuestion(),resumeItemRequestDto.getAnswer());
            ResumeFeedback feedback = ResumeFeedback.builder()
                .resumeItem(resumeItem)
                .feedbackText(feedbackText)
                .build();
            resumeFeedbackRepository.save(feedback);
        }
        return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
    }

    public List<ResumeItemResponseDto> getResume(Long resumeId) {
        Resume resume = resumeService.findById(resumeId);
        List<ResumeItem> resumeItems = resumeItemRepository.findByResumeId(resume.getId()).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_RESUME_ITEM)
        );

        return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
    }

    @Transactional
    public ResumeItemResponseDto updateResumeItem(Long resumeItemId,
        ResumeItemRequestDto resumeItemRequestDto) {
        ResumeItem resumeItem = findById(resumeItemId);
        resumeItem.updateResumeItem(resumeItemRequestDto);
        resumeItemRepository.save(resumeItem);

        return new ResumeItemResponseDto(resumeItem);
    }

    @Transactional
    public void deleteResumeItem(Long resumeItemId) {
        ResumeItem resumeItem = findById(resumeItemId);
        resumeItemRepository.delete(resumeItem);
    }

    @Transactional
    public List<ResumeItemResponseDto> uploadResumeFile(Long resumeId,MultipartFile file) {
        Resume resume = resumeService.findById(resumeId);
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
                if (section.isEmpty()) continue;

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

                String feedbackText = resumeFeedbackService.createResumeFeedback(resume.getApplication(),question,answer);
                ResumeFeedback feedback = ResumeFeedback.builder()
                    .resumeItem(resumeItem)
                    .feedbackText(feedbackText)
                    .build();
                resumeFeedbackRepository.save(feedback);
            }

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
}
