package org.nexusscode.backend.survey.init;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.survey.domain.DeveloperType;
import org.nexusscode.backend.survey.repository.DeveloperTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeveloperTypeInitializer implements CommandLineRunner {

    private final DeveloperTypeRepository developerTypeRepository;

    @Override
    public void run(String... args) {
        if (developerTypeRepository.count() == 0) {
            developerTypeRepository.save(DeveloperType.builder()
                .name("실행 중심 개발자")
                .description("문제를 빠르게 정의하고 해결책을 실행으로 옮기는 데 강한 역량을 가진 개발자입니다. 계획보다는 실험과 구현을 통해 결과를 도출하며, MVP를 빠르게 완성하고 검증하는 것을 선호합니다.")
                .keywords(List.of("실행력", "신속함", "목표 지향", "현실적인", "프로토타이핑"))
                .build());

            developerTypeRepository.save(DeveloperType.builder()
                .name("협업형 개발자")
                .description("다른 사람과의 소통과 협업을 통해 시너지를 내는 개발자입니다. 팀 내 역할 분담, 피드백 반영, 갈등 해결 등에서 두각을 나타내며 조직 문화를 중요하게 여깁니다.")
                .keywords(List.of("소통", "공감", "팀워크", "협업", "리더십"))
                .build());

            developerTypeRepository.save(DeveloperType.builder()
                .name("문제 해결형 개발자")
                .description("복잡한 문제를 깊이 있게 분석하고, 다양한 접근법으로 해결책을 찾아내는 개발자입니다. 알고리즘 최적화, 시스템 오류 추적, 복잡한 로직 개선에서 뛰어난 역량을 보입니다.")
                .keywords(List.of("분석력", "디버깅", "로직", "집요함", "창의적 사고"))
                .build());

            developerTypeRepository.save(DeveloperType.builder()
                .name("가치 지향 개발자")
                .description("코드의 품질과 개발 철학을 중요하게 생각하는 개발자입니다. 장기적인 유지보수성과 확장성을 고려한 아키텍처 설계, 클린 코드 작성, 테스트 중심 개발 등에 높은 관심을 가집니다.")
                .keywords(List.of("클린코드", "아키텍처", "테스트", "리팩토링", "장기적 가치"))
                .build());
        }
    }
}

