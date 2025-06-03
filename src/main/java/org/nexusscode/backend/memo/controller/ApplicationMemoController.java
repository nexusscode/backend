package org.nexusscode.backend.memo.controller;

import lombok.RequiredArgsConstructor;

import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.memo.dto.MemoRequestDTO;
import org.nexusscode.backend.memo.dto.MemoResponseDTO;
import org.nexusscode.backend.memo.service.ApplicationMemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class ApplicationMemoController {

    // 서비스 객체를 가져와서 사용 (생성자 주입 방식)
    private final ApplicationMemoService memoService;


    //사용자 아이디를 X-USER-ID로 전달받는다고 가정했습니다.

    // 메모를 새로 작성하는 API
    @Operation(summary = "지원서 메모 생성", description = "새로운 지원서 메모를 생성합니다.")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping
    public ResponseEntity<CommonResponse<MemoResponseDTO>> createMemo(@RequestHeader Long userId,
                                                      @RequestBody MemoRequestDTO requestDTO) {
        // 메모 작성 기능 호출 (사용자 ID와 메모 내용 전달)
        MemoResponseDTO response = memoService.createMemo(userId, requestDTO);
        // 작성된 메모를 응답으로 반환
        return ResponseEntity.ok(new CommonResponse<>("메모가 생성되었습니다.", 200, response));
    }

    // 내 메모 전체 리스트 가져오기
    @GetMapping
    public ResponseEntity<CommonResponse<List<MemoResponseDTO>>> getUserMemos(@RequestHeader Long userId) {
        // 사용자 ID로 메모 리스트 조회
        List<MemoResponseDTO> response = memoService.getUserMemos(userId);
        return ResponseEntity.ok(new CommonResponse<>("사용자 메모 목록 조회 성공", 200, response));
    }

    // 메모 하나 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MemoResponseDTO>> getMemoDetail(@RequestHeader Long userId,
                                                         @PathVariable Long memoId) {
        // 특정 메모 ID로 상세정보 조회
        MemoResponseDTO response = memoService.getMemoDetail(userId, memoId);
        return ResponseEntity.ok(new CommonResponse<>("메모 상세 조회 성공", 200, response));
    }

    // 메모 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<MemoResponseDTO>> updateMemo(@RequestHeader Long userId,
                                                      @PathVariable Long id,
                                                      @RequestBody MemoRequestDTO requestDto) {
        // 메모 ID와 수정할 내용으로 메모 업데이트
        MemoResponseDTO response = memoService.updateMemo(userId, id, requestDto);
        return ResponseEntity.ok(new CommonResponse<>("메모가 수정되었습니다.", 200, response));
    }

    // 메모 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Object>> deleteMemo(@RequestHeader Long userId,
                                           @PathVariable Long memoId) {
        // 메모 ID로 삭제 기능 호출
        memoService.deleteMemo(userId, memoId);
        // 삭제 완료 응답
        return ResponseEntity.ok(new CommonResponse<>("메모가 삭제되었습니다.", 200, null));
    }
}
