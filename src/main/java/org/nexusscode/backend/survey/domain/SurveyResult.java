package org.nexusscode.backend.survey.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "survey_results")
public class SurveyResult extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "dominance_score")
    private int dominanceScore;

    @Column(name = "influence_score")
    private int influenceScore;

    @Column(name = "steadiness_score")
    private int steadinessScore;

    @Column(name = "conscientiousness_score")
    private int conscientiousnessScore;

    @ManyToOne
    @JoinColumn(name = "disc_type")
    private DiscType discType;

    @Column(name = "development_approach_scroe")
    private int developmentApproachScore;

    @Column(name = "team_collaboration_score")
    private int teamCollaborationScore;

    @Column(name = "problem_solving_score")
    private int problemSolvingScore;

    @Column(name = "development_values_score")
    private int developmentValuesScore;

    @ManyToOne
    @JoinColumn(name = "developer_type")
    private DeveloperType developerType;

    @Builder
    public SurveyResult(User user,int dominanceScore, int influenceScore, int steadinessScore,
        int conscientiousnessScore,DiscType discType,
        int developmentApproachScore, int teamCollaborationScore, int problemSolvingScore,
        int developmentValuesScore,DeveloperType developerType) {
        this.user=user;
        this.dominanceScore = dominanceScore;
        this.influenceScore = influenceScore;
        this.steadinessScore = steadinessScore;
        this.conscientiousnessScore = conscientiousnessScore;
        this.discType=discType;
        this.developmentApproachScore = developmentApproachScore;
        this.teamCollaborationScore = teamCollaborationScore;
        this.problemSolvingScore = problemSolvingScore;
        this.developmentValuesScore = developmentValuesScore;
        this.developerType=developerType;
    }

    public void updateDisc(int dScore, int iScore, int sScore, int cScore,DiscType discType) {
        this.dominanceScore=dScore;
        this.influenceScore=iScore;
        this.steadinessScore=sScore;
        this.conscientiousnessScore=cScore;
        this.discType=discType;
    }

    public void updateDev(int devApproachScore, int teamCollabScore, int problemSolvingScore, int devValuesScore,DeveloperType developerType) {
        this.developmentApproachScore=devApproachScore;
        this.teamCollaborationScore=teamCollabScore;
        this.problemSolvingScore=problemSolvingScore;
        this.developmentValuesScore=devValuesScore;
        this.developerType=developerType;
    }
}
