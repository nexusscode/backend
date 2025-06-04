package org.nexusscode.backend.survey.init;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.survey.domain.DiscType;
import org.nexusscode.backend.survey.repository.DiscTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscTypeInitializer implements CommandLineRunner {

    private final DiscTypeRepository discTypeRepository;

    @Override
    public void run(String... args) {
        if (discTypeRepository.count() == 0) {
            discTypeRepository.save(DiscType.builder()
                .name("주도형(D)")
                .description("Dominance(주도형)는 결과 중심적이며, 빠른 결단력과 강한 추진력을 가지고 있는 유형입니다. 위험을 감수하며 목표 달성에 몰두하고, 경쟁적인 환경에서 리더십을 발휘하는 데 강점을 보입니다.")
                .keywords(List.of("도전적", "목표지향", "강한 추진력"))
                .build());

            discTypeRepository.save(DiscType.builder()
                .name("사교형(I)")
                .description("Influence(사교형)는 사람들과의 관계를 중시하며, 낙천적이고 감정 표현이 풍부한 유형입니다. 팀 내에서 분위기 메이커 역할을 하며, 타인을 설득하거나 협업하는 데 능숙합니다.")
                .keywords(List.of("사교성", "낙천적", "감성적"))
                .build());

            discTypeRepository.save(DiscType.builder()
                .name("안정형(S)")
                .description("Steadiness(안정형)는 일관성과 조화를 중요하게 생각하며, 신뢰와 배려심이 깊은 유형입니다. 팀원들과의 관계 유지와 조화를 중시하는 협력 지향적 성향을 보입니다.")
                .keywords(List.of("인내심", "협력적", "안정지향"))
                .build());

            discTypeRepository.save(DiscType.builder()
                .name("신중형(C)")
                .description("Conscientiousness(신중형)는 체계적이고 분석적인 사고를 중시하며, 정확성과 규칙을 중요시하는 유형입니다. 높은 기준과 원칙을 가지고 있으며 실수를 최소화하려고 노력합니다.")
                .keywords(List.of("분석적", "계획적", "논리적"))
                .build());
        }
    }
}
