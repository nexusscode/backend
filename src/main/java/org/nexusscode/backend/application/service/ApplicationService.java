package org.nexusscode.backend.application.service;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.application.domain.Status;
import org.nexusscode.backend.application.dto.ApplicationRequestDto;
import org.nexusscode.backend.application.dto.ApplicationResponseDto;
import org.nexusscode.backend.application.dto.ApplicationSimpleDto;
import org.nexusscode.backend.application.dto.MemoRequestDto;
import org.nexusscode.backend.application.repository.JobApplicationRepository;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeFeedbackStatus;
import org.nexusscode.backend.resume.service.ResumeService;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final ResumeService resumeService;
    private final UserService userService;

    @Value("${saramin.access-key}")
    private String accessKey;

    @Value("${saramin.endpoint}")
    private String apiUrl;

    @Transactional
    public ApplicationResponseDto createApplication(Long userId, ApplicationRequestDto applicationRequestDto) {
        User user = userService.findById(userId);

        String apiURL = UriComponentsBuilder.fromHttpUrl(apiUrl)
            .queryParam("access-key", accessKey)
            .queryParam("id", applicationRequestDto.getSaraminJobId())
            .toUriString();

        System.out.println("API URL: " + apiURL);

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(apiURL).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                con.getResponseCode() == 200 ? con.getInputStream() : con.getErrorStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println("Response: " + response);

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray jobs = jsonObject.getJSONObject("jobs").optJSONArray("job");

            if (jobs == null || jobs.length() == 0) {
                throw new RuntimeException("공고 정보를 찾을 수 없습니다.");
            }

            JSONObject job = jobs.getJSONObject(0);
            String companyName = job
                .optJSONObject("company")
                .optJSONObject("detail")
                .optString("name", "회사명 없음");

            String title = job.getJSONObject("position")
                .optString("title", "제목 없음");

            String expirationTimestampStr = job.optString("expiration-timestamp", null);
            LocalDateTime expirationDate = null;
            if (expirationTimestampStr != null) {
                try {
                    long epoch = Long.parseLong(expirationTimestampStr);
                    expirationDate = Instant.ofEpochSecond(epoch)
                        .atZone(ZoneId.of("Asia/Seoul"))
                        .toLocalDateTime();
                } catch (NumberFormatException ignored) {}
            }

            String experienceLevel = job.getJSONObject("position")
                .optJSONObject("experience-level")
                .optString("name", "경력 정보 없음");

            String jobCode = job.getJSONObject("position")
                .optJSONObject("job-code")
                .optString("name", "공고 코드 정보 없음");

            String jobType = job.getJSONObject("position")
                .optJSONObject("job-type")
                .optString("name", "직업 유형 정보 없음");

            String educationLevel = job.getJSONObject("position")
                .optJSONObject("required-education-level")
                .optString("name", "학력 정보 없음");

            String location = job.getJSONObject("position")
                .optJSONObject("location")
                .optString("name", "위치 정보 없음");

            String salary = job.getJSONObject("salary")
                .optString("name", "연봉 정보 없음");

            JobApplication application = JobApplication.builder()
                .user(user)
                .saraminJobId(applicationRequestDto.getSaraminJobId())
                .companyName(companyName)
                .jobTitle(title)
                .status(Status.IN_PROGRESS)
                .expirationDate(expirationDate)
                .experienceLevel(experienceLevel)
                .jobCode(jobCode)
                .jobType(jobType)
                .requiredEducationLevel(educationLevel)
                .location(location)
                .salary(salary)
                .build();

            applicationRepository.save(application);

            // 공고 생성시 바로 자소서 생성 (1대1)
            resumeService.createResume(user,application);
            return new ApplicationResponseDto(application);

        } catch (Exception e) {
            throw new RuntimeException("사람인 API 호출 또는 공고 생성 실패", e);
        }
    }

    public ApplicationResponseDto getApplication(Long userId, Long applicationId) {
        User user = userService.findById(userId);
        JobApplication application = applicationRepository.findById(applicationId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        if(application.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_APPLICATION);
        }
        return new ApplicationResponseDto(application);
    }

    @Transactional
    public void deleteApplication(Long userId, Long applicationId) {
        User user = userService.findById(userId);
        JobApplication application = applicationRepository.findById(applicationId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND)
        );
        if(application.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_APPLICATION);
        }
        applicationRepository.delete(application);
    }

    public Page<ApplicationSimpleDto> getAllApplication(Long userId, int page, int size) {
        User user = userService.findById(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<JobApplication> applicationPage = applicationRepository.findAllByUser(user, pageable);

        return applicationPage.map(application -> {
            Resume resume = resumeService.findResumeByApplication(application);
            ResumeFeedbackStatus feedbackStatus = resume.getFeedbackStatus();
            return new ApplicationSimpleDto(application, feedbackStatus);
        });
    }

    /*@Transactional
    public String uploadDetailImage(Long applicationId, MultipartFile file) {
        JobApplication application = findById(applicationId);
        String imageText;
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            // Tesseract OCR 설정
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("/opt/homebrew/share/tessdata");
            tesseract.setLanguage("kor");

            // OCR 수행
            imageText = tesseract.doOCR(image);
        } catch (Exception e){
            System.out.println("job ocr fail : "+e.getMessage());
            throw new CustomException(ErrorCode.JOB_OCR_FAILURE);
        }
        application.updateJobDescription(imageText);
        applicationRepository.save(application);

        return imageText;
    }*/

    public Page<ApplicationSimpleDto> searchApplication(Long userId, int page, int size,String searchWord) {
        User user = userService.findById(userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<JobApplication> jobApplicationPage = applicationRepository.searchByUserAndCompanyOrTitle(user,searchWord,pageable);

        return jobApplicationPage.map(application -> {
            Resume resume = resumeService.findResumeByApplication(application);
            ResumeFeedbackStatus feedbackStatus = resume.getFeedbackStatus();
            return new ApplicationSimpleDto(application, feedbackStatus);
        });
    }

    public JobApplication findById(Long id){
        return applicationRepository.findById(id).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_APPLICATION)
        );
    }

    @Transactional
    public void updateMemo(Long userId, Long applicationId, MemoRequestDto memoRequestDto) {
        User user = userService.findById(userId);
        JobApplication application = findById(applicationId);
        if(application.getUser()!=user){
            throw new CustomException(ErrorCode.NOT_UNAUTHORIZED_APPLICATION);
        }
        application.updateMemo(memoRequestDto);
        applicationRepository.save(application);

    }
}
