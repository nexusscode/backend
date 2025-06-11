package org.nexusscode.backend.interview.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQnAStorageBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_box_id")
    private InterviewSummaryStorageBox storageBox;

    private String questionText;
    private String transcript;
    private String feedback;
    private int second;
    private boolean cheated;
    private boolean completeAnswer;
    private boolean questionFulfilled;
    private String blindKeywords;
}
