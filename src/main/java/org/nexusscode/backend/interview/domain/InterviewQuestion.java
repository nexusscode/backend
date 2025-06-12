package org.nexusscode.backend.interview.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference("session-question")
    private InterviewSession session;

    @OneToOne(mappedBy = "question", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference("question-answer")
    private InterviewAnswer answer;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    private String ttsFileName;

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
        this.ttsFileName = ttsUrl;
    }
}
