package org.nexusscode.backend.memo.dto;

import lombok.Data;

@Data
public class MemoRequestDTO {
    private Long applicationId;
    private String content;
    private boolean pinned;
}