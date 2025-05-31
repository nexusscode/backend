package org.nexusscode.backend.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nexusscode.backend.global.Timestamped;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStat extends Timestamped {
    @Id
    private Long userId;

    @Column(nullable = false)
    private int totalInterviews = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    public void increaseInterviewCount() {
        this.totalInterviews++;
    }
}
