package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.resume.dto.ResumeItemRequestDto;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "resume_items")
public class ResumeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "ai_count")
    private Long aiCount;

    @Builder
    public ResumeItem(Resume resume, String question, String answer) {
        this.resume = resume;
        this.question = question;
        this.answer = answer;
        this.aiCount=1L;
    }

    public void updateResumeItem(ResumeItemRequestDto resumeItemRequestDto) {
        this.question = resumeItemRequestDto.getQuestion();
        this.answer = resumeItemRequestDto.getAnswer();
    }

    public void updateAiCount(){
        this.aiCount++;
    }
}
