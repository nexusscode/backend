package org.nexusscode.backend.memo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemoResponseDTO {

    private Long id;
    private Long applicationId;
    private String content;
    private boolean pinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
