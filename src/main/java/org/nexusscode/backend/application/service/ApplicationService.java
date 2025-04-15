package org.nexusscode.backend.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.Career;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.application.domain.JobSource;
import org.nexusscode.backend.application.domain.Status;
import org.nexusscode.backend.application.dto.ApplicationRequestDto;
import org.nexusscode.backend.application.dto.ApplicationResponseDto;
import org.nexusscode.backend.application.dto.ApplicationUpdateRequestDto;
import org.nexusscode.backend.application.repository.JobApplicationRepository;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final JobApplicationRepository applicationRepository;

    public ApplicationResponseDto createApplication(ApplicationRequestDto applicationRequestDto) {
        Career career = checkCareer(applicationRequestDto.getCareer());
        JobSource jobSource=checkSource(applicationRequestDto.getJobSource());

        JobApplication application = JobApplication.builder()
            .companyName(applicationRequestDto.getCompanyName())
            .jobTitle(applicationRequestDto.getJobTitle())
            .status(Status.IN_PROGRESS)
            .applicationDate(applicationRequestDto.getApplicationDate())
            .career(career)
            .jobSource(jobSource)
            .build();
        applicationRepository.save(application);

        return new ApplicationResponseDto(application);
    }

    public ApplicationResponseDto updateApplication(
        ApplicationUpdateRequestDto updateRequestDto, Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        Career career = checkCareer(updateRequestDto.getCareer());
        JobSource jobSource=checkSource(updateRequestDto.getJobSource());
        Status status = checkStatus(updateRequestDto.getStatus());
        // 추후 로그인 유저의 application 맞는지 확인 필요
        application.updateApplication(updateRequestDto,career,jobSource,status);
        applicationRepository.save(application);

        return new ApplicationResponseDto(application);
    }

    public ApplicationResponseDto getApplication(Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        // 추후 로그인 유저의 application 맞는지 확인 필요
        return new ApplicationResponseDto(application);
    }

    public void deleteApplication(Long applicationId) {
        JobApplication application = applicationRepository.findById(applicationId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        // 추후 로그인 유저의 application 맞는지 확인 필요
        applicationRepository.delete(application);
    }

    public List<ApplicationResponseDto> getAllApplication() {
        List<JobApplication> applicationList = applicationRepository.findAll();
        // 추후 로그인 유저의 application 로만 리스트 조회

        return applicationList.stream().map(ApplicationResponseDto::new).toList();
    }

    public Career checkCareer(String requestCareer){
        Career career;
        if(requestCareer.equals("신입")){
            career = Career.NEW;
        } else if (requestCareer.equals("경력")) {
            career = Career.EXPERIENCED;
        } else {
            throw new CustomException(ErrorCode.INVALID_VALUE);
        }
        return career;
    }

    public JobSource checkSource(String requestSource){
        JobSource jobSource;
        if(requestSource.equals("사람인")){
            jobSource = JobSource.SARAMIN;
        } else if (requestSource.equals("원티드")) {
            jobSource = JobSource.WANTED;
        } else {
            throw new CustomException(ErrorCode.INVALID_VALUE);
        }
        return jobSource;
    }

    public Status checkStatus(String requestStatus){
        Status status;
        if(requestStatus.equals("진행")){
            status = Status.IN_PROGRESS;
        } else if (requestStatus.equals("제출")) {
            status = Status.SUBMITTED;
        }  else if (requestStatus.equals("통과")) {
            status = Status.PASSED;
        }  else if (requestStatus.equals("탈락")) {
            status = Status.FAILED;
        } else {
            throw new CustomException(ErrorCode.INVALID_VALUE);
        }
        return status;
    }
}
