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
            당신은 면접 전문가이며, 다음은 한 지원자의 면접에 대한 피드백 목록과 이전 응시 텍스트입니다. \s
            이 정보를 기반으로 **깊이 있는 분석과 정성적 비교 평가**를 수행하세요. 평가자는 실제 면접관처럼 **지원자의 응답 내용을 정확하게 이해하고**, **표현 방식, 어휘 사용, 태도, 논리성** 등을 바탕으로 객관적이고 실질적인 피드백을 작성해야 합니다.

            <입력>
            1. 면접 피드백 목록 (현재 응시):
            {inputText}

            2. 이전 응시 답변:
            {previousAttemptText}

            <요청 항목 및 출력 형식> \s
            각 항목은 최소 2~3문장의 설명을 포함해야 하며, 가능하면 예시를 들어 구체적으로 작성할 것.

            ---

            1. **강점 ** \s
            지원자의 응답에서 드러난 긍정적인 요소들을 분석해 주세요. \s
            표현력, 직무 이해도, 논리 구성, 실무 경험, 태도 등에서 나타난 강점을 **단순 나열이 아닌 문맥 중심**으로 서술해 주세요. \s
            **답변의 어떤 부분에서 어떤 방식으로 강점이 드러났는지 구체적으로 설명**해 주시기 바랍니다. \s
            예: “본인의 경험을 직무와 직접 연결하며 설명하는 과정에서 논리성과 연관성이 돋보였습니다.”

            ---

            2. **약점 ** \s
            지원자의 응답 중 미흡했던 부분, 모호한 표현, 논리적 비약, 구체성 부족 등의 약점을 분석해 주세요. \s
            단순히 “부족하다”고 평가하기보다, **어떤 표현이 어떤 이유에서 약점으로 작용했는지를 명확하게 서술**해 주세요. \s
            예: “문제 상황 설명이 구체적이지 않아 실제 경험인지 판단하기 어려웠습니다.”

            ---

            3. **AI 면접관 종합 평가 ** \s
            전체 응답을 기반으로 한 지원자의 **전반적인 커뮤니케이션 역량, 논리성, 태도, 직무 적합도**에 대한 종합 평가를 작성해 주세요. \s
            **강점과 약점의 균형**, 응답 흐름, 신뢰감, 실제 면접 상황에서 줄 수 있는 인상 등을 포괄적으로 기술해 주세요. \s
            예: “표현력은 뛰어나지만 논리 전개가 약해 설득력 면에서는 아쉬움이 있었습니다.”

            ---

            4. **이전 응시 대비 변화 및 비교 평가 ** \s
            이전 응답과 비교하여 **개선된 점, 반복되는 문제, 퇴보된 부분** 등을 정성적으로 분석해 주세요. \s
            구조가 더 명확해졌는지, 표현력이 향상되었는지, 여전히 구체성 부족 문제가 반복되고 있는지 등 **기준을 갖고 비교**해 주세요. \s
            예: “이전 응시보다 구조화는 향상되었으나 여전히 어휘 반복 현상은 지속되고 있습니다.”

            ---

            5. **어휘 평가 ** \s
            지원자의 답변에서 자주 반복된 단어를 시각적으로 표시한 자료를 참고하여 어휘 사용의 경향을 분석해 주세요. \s
            **반복된 접속어, 구어체 표현, 어휘 수준의 제한성** 등을 지적하고, 보다 풍부한 표현으로 대체할 수 있는 제안을 제시해 주세요.
            
            **출력 예시:** \s
            - 반복 사용된 단어: **그리고(5회)**, **좀(3회)**, **되게(2회)** \s
            - 어휘 수준 평가: 단어 선택이 비교적 단순하며, 반복된 어휘가 많아 표현의 다양성이 제한적으로 보입니다. \s
            - 개선 제안: \s
              - '그리고' 대신: **게다가, 또한, 나아가, 더불어** 등을 사용할 수 있습니다. \s
              - '좀' 대신: **약간, 다소, 비교적** 등의 표현으로 구체화하면 더 명확한 전달이 가능합니다.

            ---

            6. **업무 성향 분석 ** \s
            - 지원자의 응답을 통해 드러난 업무 스타일, 협업 태도, 문제 해결 방식, 책임감 등과 관련된 성향을 분석하세요. \s
            - 예: 주도적으로 문제를 해결하려는 태도, 책임 회피적 표현, 협업 선호 여부 등 \s
            - 텍스트 내에서 태도나 자세가 반영된 문장을 근거로 분석하고, 업무 시 어떤 방식으로 행동할지를 추론하세요. \s
            - 예: “답변에서 ‘함께 고민했다’, ‘의견을 조율했다’는 표현이 반복되어 협업 중심의 성향이 드러남”
            
            ---
            
            7. **개발자 스타일 분석 ** \s
            - 기술적 사고방식, 설명 방식, 표현의 논리성, 문제 접근 태도 등 개발자적 특징을 중심으로 평가하세요. \s
            - 예: 해결 중심인지, 장황하게 설명하는 경향이 있는지, 문제를 단계적으로 구조화하는지 여부 등 \s
            - 기술 용어 사용, 직관적 설명 능력, 디버깅 중심의 접근 등 실무에 투영되는 개발 스타일을 평가 \s
            - 예: “문제 해결 과정을 시간 순서대로 구조화해서 설명함으로써 논리적이고 실무형 개발자 스타일을 보임”
                    

            ---
            작성 시 유의사항: \s
            - 모든 항목은 **문장 단위로 완결성 있게 작성**해야 합니다. \s
            - “좋다/나쁘다”와 같은 단편적 표현은 지양하며, **항상 구체적인 문맥이나 예시를 기반으로 설명**해 주세요.

            출력 형식 예:
            - 강점: \s
            - 약점: \s
            - AI 면접관 종합 평가: \s
            - 이전 응시 대비 변화 및 비교 평가: \s
            - 어휘 평가: \s
            - 업무 성향 분석: \s
            - 개발자 스타일 분석: \s
            """;

            case ADVICE_TECH -> """
            아래는 한 면접 질문과 지원자의 기술적 대답입니다.
            
            해당 응답을 기반으로 기술 면접 피드백을 작성해 주세요. 다음 항목을 기준으로 평가하세요:

            [1] 기술 용어를 정확히 사용했는가? \s
            [2] 개념과 원리를 잘 이해하고 설명했는가? \s
            [3] 구조나 흐름이 논리적으로 정리되어 있는가? \s
            [4] 예시 코드, 상황, 프로젝트 경험 등 구체적인 사례를 들었는가? \s
            [5] 문장 표현이 자연스럽고 전문가다운 어투였는가? \s
            [6] **지원자의 신원이 특정될 수 있는 단어가 포함되어 있는지 확인하고, 아래 "블라인드 키워드" 항목에 정리하세요.**

            ---

             **블라인드 키워드 추출 기준 (다음 항목에만 한정):**
            - 학교 이름 또는 줄임말 (예: 서울대, 고대, 포스텍, 경희대 등) \s
            - 회사 이름 또는 브랜드명 (예: 네이버, 삼성전자, 펄어비스 등) \s
            - 지역 이름 또는 지명 (예: 강남, 분당, 부산, 제주 등) \s
            - 인명 또는 닉네임 (예: 김지훈, 민수형, 병진이형 등) \s
            - 실제 내부 부서명, 팀명, 프로젝트명 등 (예: 'AI 전략팀', '현대오토에버 스마트융합 프로젝트' 등)

            ---

             **다음 항목은 블라인드 키워드에서 제외해야 합니다:**
            - 일반 기술 용어 (예: API, TCP, MQTT, Swagger, IoT, HTTP, Spring 등) \s
            - 전공 과목 또는 학문 용어 (예: 자료구조, 운영체제, 컴퓨터 네트워크 등) \s
            - **질문에 포함되어 있는 고유명사**(예: 질문에 언급된 회사명, 기술명, 기관명 등) → **지원자의 답변 내에 등장한 경우에만 블라인드 키워드로 추출하세요**
            - 다소 낯설거나 이상하게 보이는 단어가 있더라도, 기술 용어나 전문 용어가 잘못 발음된 것일 수 있으므로 블라인드 키워드로 확정 처리하기 전에 기술명 여부를 확인하세요. 

            ---

             **추가 지침:**
            - 음성 인식 오류(예: '캐스톤' → '캡스톤')로 잘못 표기된 단어는 문맥상 적절한 표현으로 수정해 주세요. \s
            - 피드백은 단순한 칭찬 또는 비판보다는 **정확한 진단과 개선 방향을 중심**으로 작성해 주세요.

            ---

             **출력 형식:**

            피드백:
            - TCP와 UDP의 차이를 설명할 때 핵심 개념은 잘 짚었지만, 실무적인 적용 예시가 부족했습니다.
            - “UDP는 실시간성이 중요한 환경에서 사용됩니다. 예: 화상 회의” 와 같은 보완이 필요합니다.
            - 전체적으로 용어는 정확했고, 설명 흐름도 자연스러웠습니다.

            블라인드 키워드:
            - (있으면 리스트로 정리해 주세요)
            - (없으면 "없음"으로 명시)
            
            답변 완결 여부: \s
            - true 또는 false (답변이 명확히 마무리되었으면 true, 중단되거나 결론이 없으면 false)
            
            질문 충족도 판단: \s
            - true 또는 false (지원자가 질문의 핵심에 명확히 답했으면 true, 엇나가거나 본질에서 벗어났으면 false)

            질문과 대답:
            {inputText}
            """;

            case ADVICE_PER -> """
            
            아래는 한 면접 질문과 지원자의 행동 기반(인성) 대답입니다.
            
              해당 응답을 기반으로 **인성 면접 피드백**을 작성해 주세요. 다음 항목을 기준으로 평가하세요:
    
              [1] 질문의 의도(예: 협업 능력, 갈등 해결, 책임감 등)에 적절히 답변했는가? \s
              [2] 경험이 구체적이고 사실적인가? \s
              [3] 문제 상황 → 행동 → 결과 흐름이 논리적으로 구성되어 있는가? (STAR 방식) \s
              [4] 개선점이나 배운 점을 적절히 언급했는가? \s
              [5] 표현이 자연스럽고, 성실하고 책임감 있는 인상을 주는가? \s
              [6] **지원자의 신원이 특정될 수 있는 단어가 포함되어 있는지 확인하고, 아래 '블라인드 키워드' 항목에 정리하세요.**
    
              ---
    
               **블라인드 키워드 추출 기준 (다음 항목에만 한정):**
              - 학교 이름 또는 줄임말 (예: 서울대, 연세대, 부경대 등) \s
              - 회사 이름 또는 브랜드명 (예: 네이버, 삼성전자, 펄어비스 등) \s
              - 지역 이름 또는 지명 (예: 강남구, 분당, 부산 등) \s
              - 인명 또는 닉네임 (예: 김지원, 병진이형 등) \s
              - 실제 내부 부서명, 팀명, 프로젝트명 등 (예: ‘AI전략실’, ‘서비스운영팀’, ‘아르고 프로젝트’ 등)
    
              ---
    
               **다음 항목은 블라인드 키워드에서 제외해야 합니다:**
              - 일반적 직무 용어: 디자이너, 개발자, 기획자, 백엔드 등 \s
              - 전공 과목: 논리회로, 소프트웨어공학 등 \s
              - **질문에 포함된 고유명사**(예: 질문에 언급된 회사, 지명, 학교명 등) → **지원자의 답변 내에만 등장한 경우에 한해 블라인드 키워드로 추출하세요**
              - 다소 낯설거나 이상하게 보이는 단어가 있더라도, 기술 용어나 전문 용어가 잘못 발음된 것일 수 있으므로 블라인드 키워드로 확정 처리하기 전에 기술명 여부를 확인하세요. 
    
              ---
    
               **추가 지침:**
              - 음성 인식 오류(예: ‘캐스톤’ → ‘캡스톤’)로 인해 잘못 표기된 단어는 문맥상 올바른 표현으로 정정해 주세요. \s
              - 피드백은 단순 칭찬이나 비판이 아닌, **구체적인 관찰과 개선 방향을 함께 제시해 주세요.**
    
              ---
    
               **출력 형식:**
    
              피드백:
              - 협업 경험은 구체적이었지만, 본인의 역할이나 행동 중심으로 정리가 부족했습니다.
              - “당시 저는 팀 회의를 주도하며 ~”처럼 행동 중심으로 다시 정리하면 더 설득력 있습니다.
              - 전체적으로 진정성 있는 태도가 느껴졌고, 좋은 인상을 주는 답변이었습니다.
    
              블라인드 키워드:
              - ... (있으면 리스트로 작성)
              - ... (없으면 “없음”이라고 명시)
              
              답변 완결 여부: \s
              - true 또는 false (답변이 명확히 마무리되었으면 true, 중단되거나 결론이 없으면 false)
            
              질문 충족도 판단: \s
              - true 또는 false (지원자가 질문의 핵심에 명확히 답했으면 true, 엇나가거나 본질에서 벗어났으면 false)
    
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

    public static String makeTextFromInterviewQAndA(InterviewQuestion answer) {
        return "Q: " + answer.getQuestionText() + "\n  A: " + answer.getAnswer().getTranscript() + "\n\n";
    }
}
