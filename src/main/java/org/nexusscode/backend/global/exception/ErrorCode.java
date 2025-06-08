package org.nexusscode.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.json.HTTP;
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
    NULL_FIELD_VALUE(HttpStatus.BAD_REQUEST, "필수 입력 값이 누락되었습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),
    TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 타입이 존재하지 않습니다."),
    GPT_RESPONSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GPT 응답 처리 중 오류가 발생했습니다"),
    GPT_ANALYSIS_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "GPT 분석 처리에 실패했습니다."),
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
    MALFORMED_JWT(HttpStatus.BAD_REQUEST, "Malformed JWT"),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "Expired JWT"),
    INVALID_CLAIM(HttpStatus.UNAUTHORIZED, "Invalid JWT claims"),
    NO_AUTH_HEADER(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없습니다."),
    JWT_VALIDATION_ERROR(HttpStatus.UNAUTHORIZED, "JWT 토큰 검증 중 오류가 발생했습니다."),
    TOKEN_TAMPERED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 위조되었거나 재사용되었습니다."),
    KAKAO_USER_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 사용자 정보를 가져오지 못했습니다."),
    EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, "이메일 제공에 동의하지 않았거나 이메일 정보가 없습니다."),
    API_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "요청 횟수를 초과했습니다."),
    DUPLICATE_REQUEST(HttpStatus.CONFLICT, "중복 요청이 감지되었습니다."),
    INCORRECT_DEV_TYPE(HttpStatus.BAD_REQUEST,"옳지 않은 개발 종류 입니다."),
    INCORRECT_EXPERIENCE_LEVEL(HttpStatus.BAD_REQUEST,"옳지 않은 경험 종류 입니다."),
    NOT_FOUND_SMS_VERIFICATION(HttpStatus.NOT_FOUND,"인증 문자를 찾을 수 없습니다."),
    EXPIRED_SMS_VERIFICATION(HttpStatus.NOT_FOUND,"만료된 인증 문자 입니다."),
    NOT_UNAUTHORIZED_APPLICATION(HttpStatus.FORBIDDEN,"접근 권한이 없는 공고입니다."),
    NOT_UNAUTHORIZED_RESUME(HttpStatus.FORBIDDEN,"접근 권한이 없는 자소서입니다."),
    NOT_CONNECT_SESSION(HttpStatus.NOT_FOUND, "최근에 끊긴 세션이 없습니다."),
    NOT_FOUND_DISC_TYPE(HttpStatus.NOT_FOUND,"DISC 유형을 찾을 수 없습니다."),
    NOT_FOUND_DEV_TYPE(HttpStatus.NOT_FOUND,"개발 유형을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
