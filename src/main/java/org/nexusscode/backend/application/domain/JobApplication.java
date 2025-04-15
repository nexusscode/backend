package org.nexusscode.backend.application.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.application.dto.ApplicationRequestDto;
import org.nexusscode.backend.application.dto.ApplicationUpdateRequestDto;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.domain.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class JobApplication extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "job_title")
    private String jobTitle;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    private Career career;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_source")
    private JobSource jobSource;

    public void updateApplication(ApplicationUpdateRequestDto updateRequestDto,Career career,JobSource jobSource,Status status) {
        this.companyName= updateRequestDto.getCompanyName();
        this.jobTitle= updateRequestDto.getJobTitle();
        this.status=status;
        this.applicationDate=updateRequestDto.getApplicationDate();
        this.career=career;
        this.jobSource=jobSource;
    }
}
