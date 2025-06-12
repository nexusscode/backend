package org.nexusscode.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.repository.JobApplicationRepository;
import org.nexusscode.backend.applicationReportMemo.repository.ApplicationReportMemoRepository;
import org.nexusscode.backend.applicationReportMemo.repository.ReportMemoInputSetRepository;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.nexusscode.backend.interview.repository.InterviewStorageBoxRepository;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.nexusscode.backend.survey.repository.SurveyResultRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.repository.UserStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDeleteService {
    private final JobApplicationRepository applicationRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final ResumeRepository resumeRepository;
    private final UserStatRepository userStatRepository;
    private final InterviewSessionRepository interviewSessionRepository;
    private final InterviewStorageBoxRepository interviewStorageBoxRepository;
    private final ApplicationReportMemoRepository applicationReportMemoRepository;
    private final ReportMemoInputSetRepository reportMemoInputSetRepository;

    @Transactional
    public void deleteUser(User user) {
        resumeRepository.deleteAllByUser(user);
        applicationRepository.deleteAllByUser(user);
        surveyResultRepository.findByUser(user).ifPresent(surveyResultRepository::delete);
        interviewSessionRepository.deleteAllByUser(user);
        interviewStorageBoxRepository.deleteAllByUser(user);
        userStatRepository.deleteAllByUser(user);
        applicationReportMemoRepository.deleteAllByUser(user);
        reportMemoInputSetRepository.deleteAllByUser(user);
    }
}
