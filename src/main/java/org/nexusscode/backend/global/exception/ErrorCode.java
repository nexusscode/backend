package org.nexusscode.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD REQUEST"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"),

    INVALID_VALUE(HttpStatus.BAD_REQUEST,"유효하지 않은 값 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 타입이 존재하지 않습니다."),
    GPT_RESPONSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GPT 응답 처리 중 오류가 발생했습니다"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력 값입니다"),
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "자기소개서를 찾을 수 없습니다"),
    RESUME_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "자기소개서 항목들을 찾을 수 없습니다."),
    QUESTION_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "면접 질문 저장에 실패했습니다"),
    FEEDBACK_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "피드백 저장에 실패했습니다"),
    SUMMARY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "면접 요약 저장에 실패했습니다"),
    SESSION_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "면접 세션 저장에 실패했습니다"),
    ANSWER_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "답변 저장에 실패했습니다"),
    STT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STT 변환에 실패했습니다"),
    SUMMARY_NOT_FOUND(HttpStatus.NOT_FOUND, "면접 요약을 찾을 수 없습니다"),
    SESSION_LIST_EMPTY(HttpStatus.NOT_FOUND, "면접 세션이 존재하지 않습니다"),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 세션에 질문이 존재하지 않습니다"),
    TTS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TTS 변환에 실패했습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TTS S3 업로드에 실패했습니다."),
    S3_PRESIGN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_PresignUrl 생성에 실패했습니다."),
    NOT_FOUND_APPLICATION(HttpStatus.NOT_FOUND, "등록된 공고를 찾을 수 없습니다."),
    NOT_FOUND_RESUME(HttpStatus.NOT_FOUND, "등록된 자소서를 찾을 수 없습니다."),
    NOT_FOUND_RESUME_ITEM(HttpStatus.NOT_FOUND, "등록된 자소서 항목을 찾을 수 없습니다."),
    NOT_FOUND_SURVEY_RESULT(HttpStatus.NOT_FOUND, "등록된 설문 결과를 찾을 수 없습니다."),
    JOB_OCR_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR,"공고 상세 이미지 분석에 실패하였습니다."),
    UPLOAD_RESUME_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR,"자소서 파일 업로드에 실패하였습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다."),
    ALREADY_SAVED_RESUME(HttpStatus.BAD_REQUEST,"이미 보관함에 저장된 자소서입니다."),
    NOT_SAVED_RESUME(HttpStatus.BAD_REQUEST,"보관함에 존재하지 않는 자소서입니다."),
    INCORRECT_DEV_TYPE(HttpStatus.BAD_REQUEST,"옳지 않은 개발 종류 입니다."),
    INCORRECT_EXPERIENCE_LEVEL(HttpStatus.BAD_REQUEST,"옳지 않은 경험 종류 입니다.");

    private final HttpStatus status;
    private final String message;
}
