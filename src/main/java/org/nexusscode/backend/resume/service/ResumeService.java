package org.nexusscode.backend.resume.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.application.service.ApplicationService;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;
import org.nexusscode.backend.resume.dto.ResumeItemResponseDto;
import org.nexusscode.backend.resume.dto.ResumeRequestDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ApplicationService applicationService;
    private final ResumeRepository resumeRepository;

    @Transactional
    public ResumeResponseDto createResume(Long applicationId, ResumeRequestDto resumeRequestDto) {
        JobApplication application = applicationService.findById(applicationId);
        Resume resume = Resume.builder()
            .application(application)
            .title(resumeRequestDto.getTitle())
            .build();
        resumeRepository.save(resume);
        return new ResumeResponseDto(resume);
    }

    public List<ResumeResponseDto> getAllResumes(Long applicationId) {
        JobApplication application = applicationService.findById(applicationId);
        List<Resume> resumeList = resumeRepository.findAllByApplication(application);

        return resumeList.stream().map(ResumeResponseDto::new).toList();
    }

    @Transactional
    public ResumeResponseDto updateResume(Long resumeId, ResumeRequestDto resumeRequestDto) {
        Resume resume = findById(resumeId);
        resume.updateResume(resumeRequestDto.getTitle());
        resumeRepository.save(resume);

        return new ResumeResponseDto(resume);
    }

    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = findById(resumeId);
        resumeRepository.delete(resume);
    }

    @Transactional
    public void saveResumeInArchieve(Long resumeId) {
        Resume resume = findById(resumeId);

        if(resume.isSaved()){
            throw new CustomException(ErrorCode.ALREADY_SAVED_RESUME);
        }
        resume.updateSaveStatus(true);
        resumeRepository.save(resume);
    }

    @Transactional
    public void cancelResumeFromArchieve(Long resumeId) {
        Resume resume = findById(resumeId);
        if(!resume.isSaved()){
            throw new CustomException(ErrorCode.NOT_SAVED_RESUME);
        }
        resume.updateSaveStatus(false);
        resumeRepository.save(resume);
    }

    public Resume findById(Long id) {
        return resumeRepository.findById(id).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_RESUME)
        );
    }

    public void save(Resume resume){
        resumeRepository.save(resume);
    }
}
