package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "answer")
public class InterviewQuestion extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    @JsonBackReference
    private InterviewSession session;

    @OneToOne(mappedBy = "question", fetch = FetchType.LAZY)
    @JsonBackReference
    private InterviewAnswer answer;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String TTSFileName;

    @Column(columnDefinition = "TEXT")
    private String intentText;

    @Enumerated(EnumType.STRING)
    private InterviewType interviewType;

    private int seq;

    public void saveSession(InterviewSession session) {
        this.session = session;
    }

    public void saveAnswer(InterviewAnswer answer) {
        this.answer = answer;
    }

    public void saveTTSFileName(String ttsUrl) {
        this.TTSFileName = ttsUrl;
    }
}
