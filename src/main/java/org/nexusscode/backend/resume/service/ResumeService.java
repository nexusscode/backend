package org.nexusscode.backend.resume.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.application.service.ApplicationService;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.dto.RecentResumeResponseDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ApplicationService applicationService;
    private final ResumeRepository resumeRepository;
    private final UserService userService;

    @Transactional
    public ResumeResponseDto createResume(Long userId,Long applicationId) {
        User user = userService.findById(userId);
        JobApplication application = applicationService.findById(applicationId);

        Resume resume = Resume.builder()
            .application(application)
            .user(user)
            .build();
        resumeRepository.save(resume);
        return new ResumeResponseDto(resume);
    }

    public ResumeResponseDto getResume(Long userId, Long applicationId) {
        User user = userService.findById(userId);
        JobApplication application = applicationService.findById(applicationId);
        if(application.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_APPLICATION);
        }
        Resume resume = resumeRepository.findByApplication(application);

        return new ResumeResponseDto(resume);
    }

   /* @Transactional
    public ResumeResponseDto updateResume(Long resumeId, ResumeRequestDto resumeRequestDto) {
        Resume resume = findById(resumeId);
        resume.updateResume(resumeRequestDto.getTitle());
        resumeRepository.save(resume);

        return new ResumeResponseDto(resume);
    }*/

    @Transactional
    public void deleteResume(Long userId,Long resumeId) {
        User user = userService.findById(userId);
        Resume resume = findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        resumeRepository.delete(resume);
    }

    @Transactional
    public void saveResumeInArchieve(Long userId,Long resumeId) {
        User user = userService.findById(userId);
        Resume resume = findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }

        if(resume.isSaved()){
            throw new CustomException(ErrorCode.ALREADY_SAVED_RESUME);
        }
        resume.updateSaveStatus(true);
        resumeRepository.save(resume);
    }

    @Transactional
    public void cancelResumeFromArchieve(Long userId,Long resumeId) {
        User user = userService.findById(userId);
        Resume resume = findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
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


    public Resume findResumeListByApplicationId(Long applicationId) {
        return resumeRepository.findByApplicationId(applicationId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RESUME));
    }

    public void save(Resume resume){
        resumeRepository.save(resume);

    }

    public RecentResumeResponseDto getRecentResume(Long userId) {
        User user = userService.findById(userId);
        Resume resume = resumeRepository.findTopByUserOrderByModifiedAtDesc(user);
        return new RecentResumeResponseDto(resume.getId(),resume.getApplication().getJobTitle(),resume.getModifiedAt());
    }

    public Long sumAiCountByUser(User user) {
        return resumeRepository.sumAiCountByUser(user);
    }
}
