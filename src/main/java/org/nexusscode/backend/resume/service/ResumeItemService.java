package org.nexusscode.backend.resume.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeItemService {

    private final ResumeService resumeService;
    private final ResumeItemRepository resumeItemRepository;

    public List<ResumeItemResponseDto> createResumeItems(Long resumeId,
        List<ResumeItemRequestDto> resumeItemRequestDtos) {
        Resume resume = resumeService.findById(resumeId);
        List<ResumeItem> resumeItems = new ArrayList<>();

        for (ResumeItemRequestDto resumeItemRequestDto : resumeItemRequestDtos) {
            ResumeItem resumeItem = ResumeItem.builder()
                .resume(resume)
                .question(resumeItemRequestDto.getQuestion())
                .answer(resumeItemRequestDto.getAnswer())
                .seq(resumeItemRequestDto.getSeq())
                .build();
            resumeItems.add(resumeItem);
            resume.addResumeItem(resumeItem);
            resumeItemRepository.save(resumeItem);
        }
        return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
    }

    public List<ResumeItemResponseDto> getResume(Long resumeId) {
        Resume resume = resumeService.findById(resumeId);
        List<ResumeItem> resumeItems = resumeItemRepository.findByResumeId(resume.getId());

        return resumeItems.stream().map(ResumeItemResponseDto::new).toList();
    }

    public ResumeItemResponseDto updateResumeItem(Long resumeItemId,
        ResumeItemRequestDto resumeItemRequestDto) {
        ResumeItem resumeItem = findById(resumeItemId);
        resumeItem.updateResumeItem(resumeItemRequestDto);
        resumeItemRepository.save(resumeItem);

        return new ResumeItemResponseDto(resumeItem);
    }

    public void deleteResumeItem(Long resumeItemId) {
        ResumeItem resumeItem = findById(resumeItemId);
        resumeItemRepository.delete(resumeItem);
    }

    public ResumeItem findById(Long id) {
        return resumeItemRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_RESUME_ITEM)
        );
    }
}
