package org.nexusscode.backend.resume.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.application.repository.JobApplicationRepository;
import org.nexusscode.backend.application.service.ApplicationService;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.dto.RecentResumeResponseDto;
import org.nexusscode.backend.resume.dto.ResumeResponseDto;
import org.nexusscode.backend.resume.dto.SavedResumeResponseDto;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final JobApplicationRepository applicationRepository;
    private final ResumeRepository resumeRepository;
    private final UserService userService;

    @Transactional
    public void createResume(User user ,JobApplication application) {
        Resume resume = Resume.builder()
            .application(application)
            .user(user)
            .build();
        resumeRepository.save(resume);
    }

    public ResumeResponseDto getResume(Long userId, Long applicationId) {
        User user = userService.findById(userId);
        JobApplication application = applicationRepository.findById(applicationId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_APPLICATION)
        );
        if(application.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_APPLICATION);
        }
        Resume resume = findResumeByApplication(application);

        return new ResumeResponseDto(resume);
    }

   /* @Transactional
    public ResumeResponseDto updateResume(Long resumeId, ResumeRequestDto resumeRequestDto) {
        Resume resume = findById(resumeId);
        resume.updateResume(resumeRequestDto.getTitle());
        resumeRepository.save(resume);

        return new ResumeResponseDto(resume);
    }*/

    /*@Transactional
    public void deleteResume(Long userId,Long resumeId) {
        User user = userService.findById(userId);
        Resume resume = findById(resumeId);
        if(resume.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_RESUME);
        }
        resumeRepository.delete(resume);
    }*/

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

    public Resume findResumeByApplication(JobApplication application){
        return resumeRepository.findByApplication(application);
    }

    public Page<SavedResumeResponseDto> getResumeFromArchieve(Long userId, String searchWord, int page, int size) {
        User user = userService.findById(userId);
        Pageable pageable = PageRequest.of(page, size);

        if (searchWord != null && !searchWord.isEmpty()) {
            List<SavedResumeResponseDto> list = new ArrayList<>();
            Page<JobApplication> jobApplicationPage = applicationRepository.searchByUserAndCompanyOrTitle(user, searchWord, pageable);

            for (JobApplication application : jobApplicationPage) {
                Resume resume = findResumeByApplication(application);
                if (resume.isSaved()) {
                    SavedResumeResponseDto dto = new SavedResumeResponseDto(resume.getId(), application.getCompanyName());
                    list.add(dto);
                }
            }

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), list.size());
            List<SavedResumeResponseDto> pageContent = list.subList(start, end);

            return new PageImpl<>(pageContent, pageable, list.size());
        }

        Page<Resume> resumePage = resumeRepository.findAllByUserAndIsSaved(user, true, pageable);
        return resumePage.map(resume -> new SavedResumeResponseDto(resume.getId(), resume.getApplication().getCompanyName()));
    }

}
