package org.nexusscode.backend.memo.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.domain.JobApplication;
import org.nexusscode.backend.application.repository.JobApplicationRepository;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.memo.domain.ApplicationMemo;
import org.nexusscode.backend.memo.dto.MemoRequestDTO;
import org.nexusscode.backend.memo.dto.MemoResponseDTO;
import org.nexusscode.backend.memo.repository.ApplicationMemoRepository;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationMemoService {

    // 저장소(Repository) 객체들 가져오기
    private final ApplicationMemoRepository memoRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    // 메모 작성 기능
    @Transactional
    public MemoResponseDTO createMemo(Long userId, MemoRequestDTO requestDTO) {
        // 사용자 ID로 사용자 조회 (없으면 예외 발생)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 지원 ID로 지원 내역 조회 (없으면 예외 발생)
        JobApplication application = applicationRepository.findById(requestDTO.getApplicationId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_APPLICATION));

        // 새 메모 만들기 (빌더 사용)
        ApplicationMemo memo = ApplicationMemo.builder()
                .user(user)
                .application(application)
                .content(requestDTO.getContent())
                .pinned(requestDTO.isPinned())
                .build();

        try {
            // 메모 저장
            ApplicationMemo savedMemo = memoRepository.save(memo);

            // 저장된 메모를 DTO로 변환해서 반환
            return toDto(savedMemo);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 내 메모 리스트 가져오기
    @Transactional(readOnly = true)
    public List<MemoResponseDTO> getUserMemos(Long userId) {
        try {
            // 사용자 ID로 메모 리스트 최신순으로 가져와서 DTO로 변환
            return memoRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                    .map(this::toDto) // 각 메모를 DTO로 변환
                    .collect(Collectors.toList()); // 리스트로 반환
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 메모 하나 상세 보기
    @Transactional(readOnly = true)
    public MemoResponseDTO getMemoDetail(Long userId, Long memoId) {
        // 메모 ID로 메모 찾고 사용자 ID 일치하는지 확인
        ApplicationMemo memo = memoRepository.findById(memoId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 메모를 DTO로 반환
        return toDto(memo);
    }

    // 메모 수정 기능
    @Transactional
    public MemoResponseDTO updateMemo(Long userId, Long memoId, MemoRequestDTO requestDTO) {
        // 메모 ID로 메모 찾고 사용자 ID 일치하는지 확인
        ApplicationMemo memo = memoRepository.findById(memoId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        try {
            // 내용과 고정 여부 수정
            memo.setContent(requestDTO.getContent());
            memo.setPinned(requestDTO.isPinned());

            // 수정된 메모를 DTO로 반환
            return toDto(memo);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 메모 삭제 기능
    @Transactional
    public void deleteMemo(Long userId, Long memoId) {
        // 메모 ID로 메모 찾고 사용자 ID 일치하는지 확인
        ApplicationMemo memo = memoRepository.findById(memoId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        try {
            // 메모 삭제
            memoRepository.delete(memo);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 메모 엔티티를 DTO로 바꿔주는 함수
    private MemoResponseDTO toDto(ApplicationMemo memo) {
        return MemoResponseDTO.builder()
                .id(memo.getId())
                .applicationId(memo.getApplication().getId())
                .content(memo.getContent())
                .pinned(memo.isPinned())
                .createdAt(memo.getCreatedAt())
                .updatedAt(memo.getUpdatedAt())
                .build();
    }
}
