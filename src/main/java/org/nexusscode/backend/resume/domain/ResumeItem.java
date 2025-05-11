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

    private int seq;

    @Builder
    public ResumeItem(Resume resume, String question, String answer, int seq) {
        this.resume = resume;
        this.question = question;
        this.answer = answer;
        this.seq = seq;
    }

    public void updateResumeItem(ResumeItemRequestDto resumeItemRequestDto) {
        this.question = resumeItemRequestDto.getQuestion();
        this.answer = resumeItemRequestDto.getAnswer();
        /*this.seq = resumeItemRequestDto.getSeq();*/
    }
}
