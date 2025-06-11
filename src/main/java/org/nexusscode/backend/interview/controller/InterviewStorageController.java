package org.nexusscode.backend.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.interview.domain.InterviewSummaryStorageBox;
import org.nexusscode.backend.interview.dto.InterviewAllSessionDTO;
import org.nexusscode.backend.interview.dto.InterviewSessionDTO;
import org.nexusscode.backend.interview.service.InterviewService;
import org.nexusscode.backend.interview.service.delegation.InterviewStorageBoxService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Interview Storage API", description = "인터뷰 보관함 관련 API")
@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/storage/interview")
public class InterviewStorageController {
    private final InterviewService interviewService;
    private final InterviewStorageBoxService boxService;

    @Operation(summary = "면접 세션 보관함에 저장")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping("/save/{sessionId}")
    public ResponseEntity<CommonResponse<Long>> saveStorageSession(@PathVariable Long sessionId, @RequestHeader Long userId) {
        InterviewAllSessionDTO fullSessionDetail = interviewService.getFullSessionDetail(sessionId);
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 보관함에 저장", 200, boxService.create(userId, fullSessionDetail)));
    }

    @Operation(summary = "면접 세션 보관함에서 삭제")
    @PreAuthorize("#userId == principal.userId")
    @DeleteMapping("/delete/{sessionId}")
    public ResponseEntity<CommonResponse<Boolean>> deleteStorageSession(@PathVariable Long sessionId, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 보관함에서 삭제", 200, boxService.delete(sessionId, userId)));
    }

    @Operation(summary = "면접 세션 보관함 하나 조회")
    @PreAuthorize("#userId == principal.userId")
    @DeleteMapping("/get/{sessionId}")
    public ResponseEntity<CommonResponse<InterviewAllSessionDTO>> getStorageSession(@PathVariable Long sessionId, @RequestHeader Long userId) {
        return ResponseEntity.ok(new CommonResponse<>("면접 세션 보관함에서 조회 성공", 200, InterviewAllSessionDTO.boxEntityToDTO(boxService.get(sessionId, userId))));
    }

    @Operation(summary = "면접 세션 보관함 전체 불러오기")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/storage/getall")
    public ResponseEntity<CommonResponse<Page<InterviewSessionDTO>>> getAllStorageSessions(
            @RequestHeader Long userId,
            @RequestParam(required = false, defaultValue = "")String searchWord,
            @RequestParam(defaultValue = "1")int page,
            @RequestParam(defaultValue = "10")int size
    ) {
        Page<InterviewSummaryStorageBox> list = boxService.list(userId, searchWord, page, size);

        Page<InterviewSessionDTO> result = list.map(box -> InterviewSessionDTO.builder()
                .sessionId(box.getId())
                .title(box.getSessionTitle())
                .createdAt(box.getCreatedAt())
                .build());

        return ResponseEntity.ok(new CommonResponse<>("면접 세션 보관함 전체 조회", 200, result));
    }
}
