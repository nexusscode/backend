package org.nexusscode.backend.interview.service.support;

import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSummary;
import org.nexusscode.backend.interview.domain.InterviewType;
import org.nexusscode.backend.interview.dto.InterviewAdviceDTO;
import org.nexusscode.backend.interview.dto.InterviewQuestionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDetailDto;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InterviewServiceUtil {

    public static PromptTemplate buildPrompt(PromptType type, int count) {
        String base = switch (type) {
            case RESUME -> """
            아래는 한 사람의 자기소개서 질문 및 대답입니다.

            이 내용을 바탕으로, 예상 면접 질문을 만들어주세요.
            인성 면접에 대한 질문과 기술 면접에 대한 질문을 %d개씩 순서대로 작성하고 각 질문의 의도를 알려주세요.

            형식 예:
            질문: ~~~
            의도: ~~~

            자기소개서:
            {inputText}
            """;

            case INTERVIEW -> """
            아래는 한 사람의 면접 질문 및 대답입니다.
            
            이 내용을 바탕으로 면접관들이 이 대답에 대해서 추가적으로 궁금해 할만한 부분을 예상 면접 질문으로 만들어주세요.
            **인성 면접에 대한 추가질문과 기술 면접에 대한 추가질문을 각각 %d개씩**, 순서대로 작성해주세요. 
        
            출력 형식 예시:
            질문: 팀 프로젝트에서 갈등을 해결한 경험은 무엇인가요?
            의도: 협업 및 갈등 해결 능력을 평가하기 위함
        
            질문: Spring의 Bean Scope에는 어떤 것들이 있으며, 각각의 차이는 무엇인가요?
            의도: Spring DI에 대한 이해와 실무 적용 가능성을 평가하기 위함
        
            면접 답변:
            {inputText}
            """;

            case SUMMARY -> """
            다음은 어떤 지원자에 대한 면접관들의 질문별 피드백입니다.

            각 피드백을 바탕으로 이 지원자의 다음 항목을 요약해 주세요:

            1. 강점
            2. 약점
            3. 전반적인 평가 요약
            4. 개선을 위한 조언 (선택 사항)

            형식 예:
            - 강점:
            - 약점:
            - 종합 평가:
            - 개선 조언:

            피드백 목록:
            {inputText}
            """;

            case ADVICE_TECH -> """
            아래는 한 면접 질문과 지원자의 기술적 대답입니다.
    
            질문과 대답을 바탕으로 기술 면접 답변에 대한 피드백을 작성해주세요. 다음 사항들을 고려하세요:
    
            [1] 기술 용어를 정확히 사용했는가?
            [2] 개념과 원리를 잘 이해하고 설명했는가?
            [3] 구조나 흐름이 논리적으로 정리되어 있는가?
            [4] 예시 코드, 상황, 프로젝트 경험 등 구체적인 사례를 들었는가?
            [5] 문장 표현이 자연스럽고 전문가다운 어투였는가?
            [6] **지원자의 신원이 특정될 수 있는 단어가 있는지 확인하고, 있다면 그 단어를 아래에 "블라인드 키워드" 항목으로 추출해주세요.**
                 - 예: 학교명, 지역명, 회사명, 인명, 부서명 등
    
            형식 예시:
            피드백:
            - TCP와 UDP의 차이를 설명할 때 핵심 개념은 잘 짚었지만, 실무적인 적용 예시가 부족했습니다.
            - “UDP는 실시간성이 중요한 환경에서 사용됩니다. 예: 화상 회의” 와 같은 보완이 필요합니다.
            - 전체적으로 용어는 정확했고, 설명 흐름도 자연스러웠습니다.
            
            블라인드 키워드:
            - 서울대학교
            - 강남구
            - 카카오
    
            질문과 대답:
            {inputText}
            """;

            case ADVICE_PER -> """
            아래는 한 면접 질문과 지원자의 행동 기반(인성) 대답입니다.
    
            질문과 대답을 바탕으로 인성 면접 답변에 대한 피드백을 작성해주세요. 다음 사항들을 고려하세요:
    
            [1] 질문의 의도(예: 협업 능력, 갈등 해결 등)에 적절히 답변했는가?
            [2] 경험이 구체적이고 사실적인가?
            [3] 문제 상황 → 행동 → 결과 흐름이 논리적으로 구성되어 있는가? (STAR 방식)
            [4] 개선점이나 배운 점을 적절히 언급했는가?
            [5] 표현이 자연스럽고, 성실하고 책임감 있는 인상을 주는가?
            [6] **지원자의 신원이 특정될 수 있는 단어가 있는지 확인하고, 있다면 그 단어를 아래에 "블라인드 키워드" 항목으로 추출해주세요.**
                 - 예: 학교명, 지역명, 회사명, 인명, 부서명 등
    
            형식 예시:
            피드백:
            - 협업 경험은 구체적이었지만, 본인의 역할이나 행동 중심으로 정리가 부족했습니다.
            - “당시 저는 팀 회의를 주도하며 ~”처럼 행동 중심으로 다시 정리하면 더 설득력 있습니다.
            - 전체적으로 진정성 있는 태도가 느껴졌고, 좋은 인상을 주는 답변이었습니다.
            
            블라인드 키워드:
            - 서울대학교
            - 강남구
            - 카카오
    
            질문과 대답:
            {inputText}
            """;
        };

        return type.needsCount() ? new PromptTemplate(base.formatted(count)) : new PromptTemplate(base);
    }



    public static String makeTextFromResumeItems(List<ResumeItem> items) {
        StringBuilder sb = new StringBuilder();
        for (ResumeItem item : items) {
            sb.append("Q: ").append(item.getQuestion()).append("\n");
            sb.append("A: ").append(item.getAnswer()).append("\n\n");
        }
        return sb.toString();
    }

    public static String makeTextFromInterviewDetail(List<InterviewSessionDetailDto> items) {
        StringBuilder sb = new StringBuilder();
        for (InterviewSessionDetailDto item : items) {
            sb.append("Q: ").append(item.getQuestionText()).append("\n");
            sb.append("A: ").append(item.getTranscript()).append("\n\n");
        }
        return sb.toString();
    }

    public static String makeTextFromInterviewAdvice(List<InterviewAdviceDTO> items) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (InterviewAdviceDTO item : items) {
            sb.append(i + ": ").append(item.getFeedbackText()).append("\n\n");
            i += 1;
        }
        return sb.toString();
    }

    public static List<InterviewQuestion> mapToInterviewQuestions(List<InterviewQuestionDTO> dtos, int startSeq, int count) {
        AtomicInteger counter = new AtomicInteger(startSeq);
        return IntStream.range(0, dtos.size())
                .mapToObj(i -> {
                    InterviewType type = (i < count) ? InterviewType.PERSONALITY : InterviewType.TECHNICAL;
                    return InterviewQuestion.builder()
                            .questionText(dtos.get(i).getQuestion())
                            .intentText(dtos.get(i).getIntent())
                            .seq(counter.getAndIncrement())
                            .interviewType(type)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static InterviewSummary mapToInterviewSummary(String content) {
        return InterviewSummary.builder()
                .summary(content)
                .build();
    }

    public static String makeTextFromInterviewQAndA(InterviewQuestion answer) {
        return "Q: " + answer.getQuestionText() + "\n  A: " + answer.getAnswer().getTranscript() + "\n\n";
    }
}
