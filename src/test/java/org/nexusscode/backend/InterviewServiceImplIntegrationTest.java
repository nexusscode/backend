package org.nexusscode.backend;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.repository.InterviewAnswerRepository;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.nexusscode.backend.interview.service.GeneratorService;
import org.nexusscode.backend.interview.service.InterviewService;
import org.nexusscode.backend.interview.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "MYSQL_DATABASE=interview_platform",
        "MYSQL_USER=interview_user",
        "MYSQL_PASSWORD=secure_password123",
        "AWS_PROFILE=s3-test",
        "SARAMIN_ACCESS_KEY=3213255"
})
@Log4j2
@Transactional
class InterviewServiceImplIntegrationTest {

    @Autowired
    private AwsService awsService;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry) {
        System.setProperty("aws.profile", "s3-test");
    }

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private InterviewSessionRepository interviewSessionRepository;

    @Autowired
    private InterviewAnswerRepository interviewAnswerRepository;

    @Autowired
    private GeneratorService generatorService;

    private final Long existingResumeId = 3L;
    private final Long existingApplicationId = 2L;

    @Test
    @Rollback(false)
    void startInterview_success() {
        // given
        InterviewStartRequest request = InterviewStartRequest.builder()
                .resumeId(existingResumeId)
                .title("Mock Interview")
                .interviewType(GptVoice.FABLE)
                .build();

        // when
        Long sessionId = interviewService.startInterview(request);

        // then
        assertThat(sessionId).isNotNull();

        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not saved"));
        assertThat(session.getTitle()).isEqualTo("Mock Interview");
        assertThat(session.getQuestions()).isNotEmpty();
    }

    @Test
    void getQuestions_success() {
        QuestionAndHintDTO question = interviewService.getQuestion(14L, 0);
        log.info(question.toString());
        assertThat(question).isNotNull();
    }

    @Test
    void getList_success() {
        // given
        InterviewStartRequest request = InterviewStartRequest.builder()
                .resumeId(existingResumeId)
                .title("Session List Test")
                .interviewType(GptVoice.FABLE)
                .build();
        Long result = interviewService.startInterview(request);

        // when
        List<InterviewSessionDTO> sessionList = interviewService.getList(existingApplicationId);

        for (InterviewSessionDTO interviewSessionDTO : sessionList) {
            log.info(interviewSessionDTO.toString());
        }

        // then
        assertThat(sessionList).isNotEmpty();
        assertThat(sessionList.get(0).getTitle()).isEqualTo("Session List Test");
    }

    @Test
    @Rollback(false)
    void submitAnswer_success() {
        // given

        InterviewAnswerRequest answerRequest = InterviewAnswerRequest.builder()
                .questionId(33L)
                .audioUrl("https://demo-my-testbucket-277707098184.s3.ap-northeast-2.amazonaws.com/upload/audio/example7.mp4")
                .isCheated(false)
                .build();

        // when
        Long answerId = interviewService.submitAnswer(answerRequest);

        // then
        assertThat(answerId).isNotNull();
    }

    @Test
    void getFullSessionDetail_success() {
        // given
        Long sessionId = 14L;

        // when
        InterviewAllSessionDTO detail = interviewService.getFullSessionDetail(sessionId);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getQuestions()).isNotEmpty();
        log.info(detail.toString());
    }

    @Test
    void get(){
        String gptOutput = """
                ### 인성 면접 질문
                
                1. **질문:** 도전정신을 키우기 위해 어떤 구체적인 경험을 하였고, 그 경험이 본인에게 어떤 긍정적인 영향을 미쳤나요?
                   - **의도:** 지원자의 도전정신과 성장 과정을 구체적으로 확인하고, 이를 통해 지원자의 성격과 가치관을 파악하기 위함.
                
                2. **질문:** 팀 프로젝트에서의 갈등 상황을 어떻게 해결했는지 구체적인 사례를 들어 설명해 주세요.
                   - **의도:** 지원자의 소통 및 협업 능력을 평가하고, 문제 해결 능력과 갈등 관리 능력을 알아보기 위함.
                
                3. **질문:** '완벽주의에서 벗어나기'라는 경험이 본인의 삶에 어떤 변화를 가져왔는지 설명해 주세요.
                   - **의도:** 지원자가 실패를 어떻게 받아들이고, 이를 통해 어떤 교훈을 얻었는지를 통해 개인적 성장과 자기 개발의 태도를 평가하고자 함.
                
                ### 기술 면접 질문
                
                1. **질문:** 웹 콘텐츠 개선을 위해 어떤 기술적 접근 방식을 사용할 계획인지 설명해 주세요.
                   - **의도:** 지원자의 기술적 이해도와 문제 해결 능력을 확인하고, 실무에서 어떻게 적용할 수 있는지를 평가하기 위함.
                
                2. **질문:** NoSQL과 SQL 데이터베이스를 통합하는 아키텍처 변경 경험에 대해 구체적인 기술적 도전과 해결 방법을 설명해 주세요.
                   - **의도:** 지원자가 기술적 문제를 어떻게 분석하고 해결했는지를 확인하여, 지원자의 실무 경험과 기술 역량을 평가하고자 함.
                
                3. **질문:** 펄어비스의 '야성' 가치에 비춰본 본인의 기술적 접근 방식이나 개발 철학은 무엇인지 설명해 주세요.
                   - **의도:** 지원자의 가치관과 기업 문화의 조화를 평가하고, 지원자가 회사의 목표에 어떻게 기여할 수 있는지를 파악하기 위함.
            """;

        List<InterviewQuestionDTO> questions = parseQuestionsFrom(gptOutput);
        for (InterviewQuestionDTO question : questions) {
            log.info(question.toString());
        }


    }

    private List<InterviewQuestionDTO> parseQuestionsFrom(String gptOutput) {
        List<InterviewQuestionDTO> result = new ArrayList<>();

        String[] blocks = gptOutput.split("(?=\\*?\\*?\\s*질문\\s*\\d*\\s*:)");

        for (String block : blocks) {
            String question = null;
            String intent = null;

            if (block.strip().startsWith("###")) {
                continue;
            }

            for (String line : block.strip().split("\\r?\\n")) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("###")) {
                    continue;
                }

                if (line.matches(".*질문\\s*\\d*\\s*:\\s*.*")) {
                    question = line.replaceAll(".*질문\\s*\\d*\\s*:\\s*", "").trim();
                    question = cleanText(question);
                } else if (line.matches(".*의도:\\s*.*")) {
                    intent = line.replaceAll(".*의도:\\s*", "").trim();
                    intent = cleanText(intent);
                }
            }

            if (question != null && intent != null) {
                result.add(InterviewQuestionDTO.builder()
                        .question(question)
                        .intent(intent)
                        .build());
            }
        }
        return result;
    }

    private String cleanText(String text) {
        return text.replaceAll("^[\\-\\s\\*]+", "") // 앞쪽 -, 공백, * 제거
                .replaceAll("[\\-\\s\\*]+$", "") // 뒤쪽 -, 공백, * 제거
                .trim();
    }


    @Test
    void generateQuestionVoiceSync_durationCheck() {
        // given
        InterviewQuestion question = InterviewQuestion.builder()
                .questionText("infou 개발 당시 nosql과 sql의 DB를 합치는 아키텍처 변경 경험에 대해 구체적으로 설명해 주실 수 있습니까? 이 과정에서 어떤 기술적 도전이 있었고, 이를 어떻게 해결했는지 말씀해 주세요.")
                .build();

        GptVoice voiceType = GptVoice.ECHO;

        // when
        long start = System.currentTimeMillis();
        byte[] voiceFile = generatorService.generateQuestionVoiceSync(question, voiceType);

        awsService.uploadTtsAudio(voiceFile);
        long end = System.currentTimeMillis();

        // then
        System.out.println("음성 생성에 걸린 시간: " + (end - start) + "ms");
        assertThat(voiceFile).isNotNull();
    }


}
