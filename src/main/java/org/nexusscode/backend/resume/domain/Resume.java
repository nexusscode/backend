package org.nexusscode.backend.resume.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.application.domain.JobApplication;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    private String title;

    @CreatedDate
    private LocalDateTime createdAt;
}

