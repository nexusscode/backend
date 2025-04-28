package org.nexusscode.backend.resume.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeItemService {
    private final ResumeItemRepository resumeItemRepository;

    public ResumeResponseDto createResumeItems(Long resumeId, List<ResumeItemRequestDto> resumeItemRequestDtos) {
    }

    public void updateResumeItem(Long resumeItemId) {
    }

    public void deleteResumeItem(Long resumeItemId) {
    }
}
