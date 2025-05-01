package org.nexusscode.backend.memo.controller;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.memo.dto.MemoRequestDTO;
import org.nexusscode.backend.memo.dto.MemoResponseDTO;
import org.nexusscode.backend.memo.service.ApplicationMemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memos")
@RequiredArgsConstructor
public class ApplicationMemoController {

    // 서비스 객체를 가져와서 사용 (생성자 주입 방식)
    private final ApplicationMemoService memoService;


    //사용자 아이디를 X-USER-ID로 전달받는다고 가정했습니다.

    // 메모를 새로 작성하는 API
    @PostMapping
    public ResponseEntity<MemoResponseDTO> createMemo(@RequestHeader("X-USER-ID") Long userId,
                                                      @RequestBody MemoRequestDTO requestDTO) {
        // 메모 작성 기능 호출 (사용자 ID와 메모 내용 전달)
        MemoResponseDTO response = memoService.createMemo(userId, requestDTO);
        // 작성된 메모를 응답으로 반환
        return ResponseEntity.ok(response);
    }

    // 내 메모 전체 리스트 가져오기
    @GetMapping
    public ResponseEntity<List<MemoResponseDTO>> getUserMemos(@RequestHeader("X-USER-ID") Long userId) {
        // 사용자 ID로 메모 리스트 조회
        return ResponseEntity.ok(memoService.getUserMemos(userId));
    }

    // 메모 하나 상세보기
    @GetMapping("/{id}")
    public ResponseEntity<MemoResponseDTO> getMemoDetail(@RequestHeader("X-USER-ID") Long userId,
                                                         @PathVariable Long id) {
        // 특정 메모 ID로 상세정보 조회
        return ResponseEntity.ok(memoService.getMemoDetail(userId, id));
    }

    // 메모 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<MemoResponseDTO> updateMemo(@RequestHeader("X-USER-ID") Long userId,
                                                      @PathVariable Long id,
                                                      @RequestBody MemoRequestDTO requestDto) {
        // 메모 ID와 수정할 내용으로 메모 업데이트
        return ResponseEntity.ok(memoService.updateMemo(userId, id, requestDto));
    }

    // 메모 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@RequestHeader("X-USER-ID") Long userId,
                                           @PathVariable Long id) {
        // 메모 ID로 삭제 기능 호출
        memoService.deleteMemo(userId, id);
        // 삭제 완료 응답
        return ResponseEntity.noContent().build();
    }
}
