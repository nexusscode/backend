package org.nexusscode.backend.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RateLimitStatusDTO {
    private long limit;
    private long used;
    private long remaining;
    private long secondsUntilReset;
}
