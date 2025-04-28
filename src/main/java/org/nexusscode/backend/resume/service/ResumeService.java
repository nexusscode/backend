package org.nexusscode.backend.resume.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.dto.ResumeRequestDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;

    public ResumeResponseDto getAllResumes(Long applicationId) {
    }

    public ResumeItemResponseDto getResume(Long resumeId) {
    }

    public void deleteResume(Long resumeId) {
    }

    public ResumeResponseDto createResume(Long applicationId, ResumeRequestDto resumeRequestDto) {
    }

    public void updateResume(Long resumeId) {
    }

}
