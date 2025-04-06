package org.nexusscode.backend.interview.dto;


import lombok.Data;

@Data
public class InterviewStartRequest {
    private Long resumeId;
    private String title;
    private String interviewType;
}
