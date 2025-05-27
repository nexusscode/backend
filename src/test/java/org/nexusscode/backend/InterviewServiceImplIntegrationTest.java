package org.nexusscode.backend;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.VocabularyEvaluation;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "MYSQL_DATABASE=interview_platform",
        "MYSQL_USER=interview_user",
        "MYSQL_PASSWORD=secure_password123",
        "AWS_PROFILE=s3-test",
        "SARAMIN_ACCESS_KEY=3213255",
        "OPEN_AI_APIKEY=ㅁㄴㅇㄹㄴㅇ"
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

    private final Long existingResumeId = 2L;
    private final Long existingApplicationId = 1L;

    @Test
    @Rollback(false)
    void startInterview_success() {
        // given
        InterviewStartRequest request = InterviewStartRequest.builder()
                .applicationId(existingApplicationId)
                .interviewType(GptVoice.FABLE)
                .build();

        // when
        Long sessionId = interviewService.startInterview(request, 1L);

        // then
        assertThat(sessionId).isNotNull();

        InterviewSession session = interviewSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not saved"));
        assertThat(session.getTitle()).isEqualTo("Mock Interview");
        assertThat(session.getQuestions()).isNotEmpty();
    }

    @Test
    void getQuestions_success() {
        QuestionAndHintDTO question = interviewService.getQuestion(6L, 0);
        log.info(question.toString());
        assertThat(question).isNotNull();
    }

    @Test
    void getList_success() {
        // given
        InterviewStartRequest request = InterviewStartRequest.builder()
                .applicationId(existingApplicationId)
                .interviewType(GptVoice.FABLE)
                .build();
        Long result = interviewService.startInterview(request, 1L);

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
    void submitAnswer_success() throws InterruptedException {
        // given

        InterviewAnswerRequest answerRequest = InterviewAnswerRequest.builder()
                .questionId(31L)
                .audioUrl("https://demo-my-testbucket-277707098184.s3.ap-northeast-2.amazonaws.com/upload/audio/example1.mp4")
                .isCheated(false)
                .build();
        // when
        Long answerId = interviewService.submitAnswer(answerRequest, 1L);


        // then
        assertThat(answerId).isNotNull();
    }

    @Test
    void getUserVoicePreSignUrl_success() {
        String userVoicePreSignUrl = interviewService.getUserVoicePreSignUrl("example1.mp4");

        log.info(userVoicePreSignUrl);
        assertThat(userVoicePreSignUrl).isNotNull();
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

    private static final List<String> SECTION_TITLES = List.of(
            "강점", "약점", "AI 면접관 종합 평가", "이전 응시 대비 변화 및 비교 평가", "어휘 평가", "업무 성향 분석", "개발자 스타일 분석"
    );

    public Map<String, String> parseFeedback(String fullText) {
        Map<String, String> result = new LinkedHashMap<>();

        String titlePattern = SECTION_TITLES.stream()
                .sorted((a, b) -> b.length() - a.length())
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        String sectionRegex = "(?m)^\\s*(?:\\d+\\.\\s*)?(?:[-*]*\\s*)?\\*{0,3}\\s*(" + titlePattern + ")\\s*[:：]?\\s*\\*{0,3}\\s*$";
        Pattern pattern = Pattern.compile(sectionRegex);
        Matcher matcher = pattern.matcher(fullText);

        List<int[]> sectionPositions = new ArrayList<>();
        List<String> matchedTitles = new ArrayList<>();

        while (matcher.find()) {
            sectionPositions.add(new int[]{matcher.start(), matcher.end()});
            matchedTitles.add(matcher.group(1));
        }

        for (int i = 0; i < sectionPositions.size(); i++) {
            int startIdx = sectionPositions.get(i)[1];
            int endIdx = (i + 1 < sectionPositions.size()) ? sectionPositions.get(i + 1)[0] : fullText.length();

            String title = matchedTitles.get(i);
            String content = fullText.substring(startIdx, endIdx).strip();
            result.put(title, content);

            if ("어휘 평가".equals(title)) {
                result.putAll(parseVocabularySection(content));
            }
        }

        return result;
    }


    private Map<String, String> parseVocabularySection(String content) {
        Map<String, String> vocabMap = new LinkedHashMap<>();

        String[] lines = content.split("\\r?\\n");
        String repeatedWords = "";
        String levelComment = "";
        StringBuilder suggestions = new StringBuilder();

        boolean inSuggestionSection = false;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("- 반복 사용된 단어:")) {
                repeatedWords = line.replaceFirst("- 반복 사용된 단어:\\s*", "").trim();
            } else if (line.startsWith("- 어휘 수준 평가:")) {
                levelComment = line.replaceFirst("- 어휘 수준 평가:\\s*", "").trim();
            } else if (line.startsWith("- 개선 제안:")) {
                inSuggestionSection = true;
            } else if (inSuggestionSection && !line.isBlank()) {
                suggestions.append(line).append("\n");
            }
        }

        vocabMap.put("어휘 평가 - 반복 단어", repeatedWords);
        vocabMap.put("어휘 평가 - 수준 평가", levelComment);
        vocabMap.put("어휘 평가 - 개선 제안", suggestions.toString().trim());

        return vocabMap;
    }


    @Test
    void get2() {
        String text = """
                1. **강점 ** \s
                지원자는 협업 경험에 대해 구체적으로 설명하며, 경험의 흐름을 STAR 방식으로 잘 구성했습니다. 이 과정에서 문제 상황, 행동, 결과를 체계적으로 연결하여 논리성을 부각시켰고, 특히 갈등 해결에 대한 경험을 잘 드러내어 자신이 팀에서 어떤 역할을 맡았는지를 설명하는 데 성공했습니다. 또한, 성찰적인 태도를 보이며 개선점과 배운 점을 언급하여, 자신의 성장 가능성을 잘 표현했습니다. 이는 지원자가 자기 발전에 대한 의지를 갖고 있음을 보여줍니다. 전반적으로 진정성과 책임감이 느껴지는 태도를 통해 면접관에게 긍정적인 인상을 남겼습니다.
                
                2. **약점 ** \s
                지원자의 응답에서 약점으로 지적될 수 있는 부분은 자신의 구체적인 역할이나 행동에 대한 설명이 부족하다는 점입니다. 예를 들어, "당시 저는 팀 회의를 주도하며"와 같은 개인의 기여도를 강조하는 표현이 없었기 때문에, 답변이 팀 전체의 경험처럼 느껴졌습니다. 또한, 문제 상황에서의 행동 부분에서 세부사항이 부족하여 STAR 방식의 행동 부분이 약해졌습니다. 이로 인해 답변이 다소 평면적으로 느껴졌고, 면접관이 지원자의 실제 기여도를 판단하기 어려웠습니다.
                
                3. **AI 면접관 종합 평가 ** \s
                지원자는 표현력이 뛰어나고, 논리적인 전개가 잘 이루어졌으나, 자신의 행동에 대한 구체적인 설명이 부족하여 설득력에서 아쉬움이 남았습니다. 전반적인 커뮤니케이션 역량은 높았고, 성실하고 진정성 있는 태도를 통해 긍정적인 인상을 주었습니다. 그러나 지원자의 직무 적합성을 완전히 드러내기에는 구체성 부족이 눈에 띄었습니다. 따라서, 더 명확한 개인의 기여도를 강조할 필요가 있습니다.
                
                4. **이전 응시 대비 변화 및 비교 평가 ** \s
                이전 응시와 비교했을 때, 구조적 접근은 향상되었으며, STAR 방식으로 문제 상황을 잘 설명하는 것이 눈에 띄었습니다. 그러나 여전히 자신의 역할이나 행동을 명확하게 드러내는 부분에서 반복적인 문제가 있었습니다. 이전 응시에서도 구체성 부족이 지적되었는데, 이번에도 그 문제가 여전히 남아있어 개선이 필요함을 느꼈습니다. 따라서, 구체적이고 강력한 개인의 기여도를 강조하는 데 있어 여전히 발전의 여지가 있습니다.
                
                5. **어휘 평가** \s
                - 반복 사용된 단어: **경험(4회)**, **구체적(3회)**, **중요(3회)** \s
                - 어휘 수준 평가: 지원자의 어휘 선택은 기본적으로 적절하나, 반복된 어휘가 많아 표현의 다양성이 제한적으로 보입니다. \s
                - 개선 제안: \s
                  - '경험' 대신: **사례, 일화, 상황** 등을 사용할 수 있습니다. \s
                  - '구체적' 대신: **명확한, 세부적인, 특정한** 등의 표현으로 다양성을 높일 수 있습니다. \s
                  - '중요' 대신: **핵심적인, 필수적인, 결정적인** 등의 표현을 활용하여 보다 풍부한 어휘 사용이 가능합니다.
                
                6. **업무 성향 분석 ** \s
                지원자는 협업과 갈등 해결에 대한 경험을 통해 책임감 있는 태도를 드러냈습니다. 특히, “팀 회의를 주도하며”라는 표현에서 주도적으로 문제를 해결하려는 태도가 보였고, 성찰적인 언급을 통해 자신의 성장 가능성을 인정하고 있음을 알 수 있습니다. 이러한 태도는 팀 내에서의 협업을 중시하며, 문제 해결을 위한 적극적인 접근 방식을 나타냅니다.
                
                7. **개발자 스타일 분석 ** \s
                지원자는 기술 용어를 정확히 사용하며, 개념에 대한 기본적인 이해를 바탕으로 정보를 전달했습니다. 그러나 설명의 깊이를 높이기 위한 실무적인 적용 예시가 부족하여, 문제 해결에 대한 접근 방식이 다소 평면적으로 느껴졌습니다. 또한, STAR 방식으로 경험을 구조화하여 설명하는 모습에서 논리적이고 실무형 개발자 스타일을 보였으나, 더 깊이 있는 사례나 예시가 포함된다면 더욱 전문적인 인상을 줄 수 있을 것입니다.

                """;

        String text2 = """
                - **강점 ** \s
                지원자는 협업 경험에 대해 구체적으로 설명하며, 경험의 흐름을 STAR 방식으로 잘 구성했습니다. 이 과정에서 문제 상황, 행동, 결과를 체계적으로 연결하여 논리성을 부각시켰고, 특히 갈등 해결에 대한 경험을 잘 드러내어 자신이 팀에서 어떤 역할을 맡았는지를 설명하는 데 성공했습니다. 또한, 성찰적인 태도를 보이며 개선점과 배운 점을 언급하여, 자신의 성장 가능성을 잘 표현했습니다. 이는 지원자가 자기 발전에 대한 의지를 갖고 있음을 보여줍니다. 전반적으로 진정성과 책임감이 느껴지는 태도를 통해 면접관에게 긍정적인 인상을 남겼습니다.
                
                - **약점 ** \s
                지원자의 응답에서 약점으로 지적될 수 있는 부분은 자신의 구체적인 역할이나 행동에 대한 설명이 부족하다는 점입니다. 예를 들어, "당시 저는 팀 회의를 주도하며"와 같은 개인의 기여도를 강조하는 표현이 없었기 때문에, 답변이 팀 전체의 경험처럼 느껴졌습니다. 또한, 문제 상황에서의 행동 부분에서 세부사항이 부족하여 STAR 방식의 행동 부분이 약해졌습니다. 이로 인해 답변이 다소 평면적으로 느껴졌고, 면접관이 지원자의 실제 기여도를 판단하기 어려웠습니다.
                
                - **AI 면접관 종합 평가 ** \s
                지원자는 표현력이 뛰어나고, 논리적인 전개가 잘 이루어졌으나, 자신의 행동에 대한 구체적인 설명이 부족하여 설득력에서 아쉬움이 남았습니다. 전반적인 커뮤니케이션 역량은 높았고, 성실하고 진정성 있는 태도를 통해 긍정적인 인상을 주었습니다. 그러나 지원자의 직무 적합성을 완전히 드러내기에는 구체성 부족이 눈에 띄었습니다. 따라서, 더 명확한 개인의 기여도를 강조할 필요가 있습니다.
                
                - **이전 응시 대비 변화 및 비교 평가 ** \s
                이전 응시와 비교했을 때, 구조적 접근은 향상되었으며, STAR 방식으로 문제 상황을 잘 설명하는 것이 눈에 띄었습니다. 그러나 여전히 자신의 역할이나 행동을 명확하게 드러내는 부분에서 반복적인 문제가 있었습니다. 이전 응시에서도 구체성 부족이 지적되었는데, 이번에도 그 문제가 여전히 남아있어 개선이 필요함을 느꼈습니다. 따라서, 구체적이고 강력한 개인의 기여도를 강조하는 데 있어 여전히 발전의 여지가 있습니다.
                
                - **어휘 평가** \s
                - 반복 사용된 단어: **경험(4회)**, **구체적(3회)**, **중요(3회)** \s
                - 어휘 수준 평가: 지원자의 어휘 선택은 기본적으로 적절하나, 반복된 어휘가 많아 표현의 다양성이 제한적으로 보입니다. \s
                - 개선 제안: \s
                  - '경험' 대신: **사례, 일화, 상황** 등을 사용할 수 있습니다. \s
                  - '구체적' 대신: **명확한, 세부적인, 특정한** 등의 표현으로 다양성을 높일 수 있습니다. \s
                  - '중요' 대신: **핵심적인, 필수적인, 결정적인** 등의 표현을 활용하여 보다 풍부한 어휘 사용이 가능합니다.
                
                - **업무 성향 분석 ** \s
                지원자는 협업과 갈등 해결에 대한 경험을 통해 책임감 있는 태도를 드러냈습니다. 특히, “팀 회의를 주도하며”라는 표현에서 주도적으로 문제를 해결하려는 태도가 보였고, 성찰적인 언급을 통해 자신의 성장 가능성을 인정하고 있음을 알 수 있습니다. 이러한 태도는 팀 내에서의 협업을 중시하며, 문제 해결을 위한 적극적인 접근 방식을 나타냅니다.
                
                - **개발자 스타일 분석 ** \s
                지원자는 기술 용어를 정확히 사용하며, 개념에 대한 기본적인 이해를 바탕으로 정보를 전달했습니다. 그러나 설명의 깊이를 높이기 위한 실무적인 적용 예시가 부족하여, 문제 해결에 대한 접근 방식이 다소 평면적으로 느껴졌습니다. 또한, STAR 방식으로 경험을 구조화하여 설명하는 모습에서 논리적이고 실무형 개발자 스타일을 보였으나, 더 깊이 있는 사례나 예시가 포함된다면 더욱 전문적인 인상을 줄 수 있을 것입니다.

                """;




        log.info("-------------------------------");
        log.info(parseFeedback(text));
        log.info(parseFeedback(text2));

    }

    public static VocabularyEvaluation parse(String rawText) {
        String repeatedWordsSummary = null;
        String levelComment = null;
        StringBuilder improvementSuggestions = new StringBuilder();

        String[] lines = rawText.split("\\r?\\n");
        boolean inSuggestionSection = false;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("- 반복 사용된 단어:")) {
                repeatedWordsSummary = line.replaceFirst("- 반복 사용된 단어:\\s*", "").trim();
                inSuggestionSection = false;
            } else if (line.startsWith("- 어휘 수준 평가:")) {
                levelComment = line.replaceFirst("- 어휘 수준 평가:\\s*", "").trim();
                inSuggestionSection = false;
            } else if (line.startsWith("- 개선 제안:")) {
                inSuggestionSection = true;
                improvementSuggestions.setLength(0); // clear
            } else if (inSuggestionSection) {
                if (!line.isBlank()) {
                    improvementSuggestions.append(line).append("\n");
                }
            }
        }

        return new VocabularyEvaluation(
                repeatedWordsSummary != null ? repeatedWordsSummary : "",
                levelComment != null ? levelComment : "",
                improvementSuggestions.toString().trim()
        );
    }

    @Test
    void get3() {
        String text = """
                - 반복 사용된 단어: 그리고(5회), 구체적(4회), 경험(3회)
                - 어휘 수준 평가: 단어 선택이 비교적 단순하며, 반복된 어휘가 많아 표현의 다양성이 제한적으로 보입니다.
                - 개선 제안:
                  - '그리고' 대신: 게다가, 또한, 나아가, 더불어
                  - '구체적' 대신: 명확한, 상세한, 특정한
                """;

        log.info(parse(text).toString());

    }


}
