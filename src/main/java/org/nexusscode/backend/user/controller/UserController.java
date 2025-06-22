package org.nexusscode.backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.security.dto.TokenResponseDTO;
import org.nexusscode.backend.security.jwt.JWTProvider;
import org.nexusscode.backend.security.repository.RedisRefreshTokenRepository;
import org.nexusscode.backend.user.dto.AiCountResponsedto;
import org.nexusscode.backend.user.dto.UserDTO;
import org.nexusscode.backend.user.dto.UserModifyDTO;
import org.nexusscode.backend.user.dto.EmailFindRequestDto;
import org.nexusscode.backend.user.dto.PasswordFindRequestDto;
import org.nexusscode.backend.user.dto.ProfileResponseDto;
import org.nexusscode.backend.user.dto.ProfileUpdateRequestDto;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.service.UserService;
import org.nexusscode.backend.user.service.UserStatService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "User API",description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Log4j2
public class UserController {

    private final UserService userService;
    private final JWTProvider jwtProvider;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;
    private final UserStatService userStatService;

    @Operation(summary = "회원 가입 메서드")
    @PreAuthorize("permitAll()")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody UserRequestDto userRequestDto){
        userService.signup(userRequestDto);
        CommonResponse response = new CommonResponse("회원가입이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "회원 수정 및 토큰 수정")
    @PreAuthorize("#memberModifyDTO.email == authentication.name")
    @PutMapping("/modify")
    public ResponseEntity<CommonResponse<TokenResponseDTO>> modify(@RequestBody UserModifyDTO memberModifyDTO,
                                                                   @AuthenticationPrincipal UserDTO userDTO) {
        log.info("member modify: {}", memberModifyDTO);

        userService.modifyUser(memberModifyDTO);

        userDTO.update(memberModifyDTO.getPassword(), memberModifyDTO.getName());

        Map<String, Object> claims = userDTO.getClaims();

        String newAccessToken = jwtProvider.generateAccessToken(claims);
        String newRefreshToken = jwtProvider.generateRefreshToken(claims);

        String userId = (String) claims.get("userId");
        String name = (String) claims.get("name");
        redisRefreshTokenRepository.saveRefreshToken(userId, newRefreshToken, Duration.ofDays(1));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(1))
                .build();

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(newAccessToken, userId, name);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new CommonResponse<>("유저 정보 수정 및 토큰 재발급 완료", 200, tokenResponseDTO));
    }

    @Operation(summary = "kakao 로그인")
    @PreAuthorize("permitAll()")
    @GetMapping("/kakao")
    public ResponseEntity<CommonResponse<TokenResponseDTO>> getMemberFromKakao(@RequestParam("accessToken") String code) {
        UserDTO memberDTO = userService.requestAccessTokenFromKakao(code);

        Map<String, Object> claims = memberDTO.getClaims();

        String jwtAccessToken = jwtProvider.generateAccessToken(claims);
        String jwtRefreshToken = jwtProvider.generateRefreshToken(claims);

        String userId = (String) claims.get("userId");
        String name = (String) claims.get("name");
        redisRefreshTokenRepository.saveRefreshToken(userId, jwtRefreshToken, Duration.ofDays(1));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwtRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(1))
                .build();

        TokenResponseDTO responseDTO = new TokenResponseDTO(jwtAccessToken, userId, name);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new CommonResponse<>("카카오 로그인 완료", 200, responseDTO));
    }

    @Operation(summary = "로그아웃 메서드")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Boolean>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.NO_AUTH_HEADER);
        }

        String token = authHeader.substring(7);
        Map<String, Object> claims = jwtProvider.validateToken(token);

        Long userId = ((Number) claims.get("userId")).longValue();

        redisRefreshTokenRepository.deleteRefreshToken(userId.toString());

        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(0)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
            .body(new CommonResponse<>("로그아웃이 완료되었습니다.", 200, true));
    }

    @Operation(summary = "아이디 찾기")
    @PostMapping("/find/email")
    public ResponseEntity<CommonResponse> findEmail(@RequestBody EmailFindRequestDto emailFindRequestDto){
        String email = userService.findEmail(emailFindRequestDto);
        CommonResponse response = new CommonResponse("아이디 찾기가 완료되었습니다.",200,email);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @Operation(summary = "비밀번호 찾기")
    @PostMapping("/find/password")
    public ResponseEntity<CommonResponse> findPassword(@RequestBody PasswordFindRequestDto passwordFindRequestDto){
        String password = userService.findPassword(passwordFindRequestDto);
        CommonResponse response = new CommonResponse("비밀번호 찾기가 완료되었습니다.",200,password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(summary = "프로필 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping
    public ResponseEntity<CommonResponse<ProfileResponseDto>> getProfile(@RequestHeader Long userId){
        ProfileResponseDto responseDto = userService.getProfile(userId);
        CommonResponse<ProfileResponseDto> response = new CommonResponse<>("프로필 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @Operation(summary = "프로필 수정")
    @PreAuthorize("#userId == principal.userId")
    @PutMapping
    public ResponseEntity<CommonResponse<ProfileResponseDto>> updateProfile(@RequestHeader Long userId,@RequestBody
        ProfileUpdateRequestDto profileUpdateRequestDto){
        ProfileResponseDto responseDto = userService.updateProfile(userId, profileUpdateRequestDto);
        CommonResponse<ProfileResponseDto> response = new CommonResponse<>("프로필 수정이 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "유저별 총 AI피드백 횟수 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/feedback-count")
    public ResponseEntity<CommonResponse<AiCountResponsedto>> getFeedbackCount(@RequestHeader Long userId){
        AiCountResponsedto responseDto = userStatService.getFeedbackCount(userId);
        CommonResponse<AiCountResponsedto> response = new CommonResponse<>("AI피드백 횟수 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "카카오 로그인 URL 반환")
    @PreAuthorize("permitAll()")
    @GetMapping("/kakao/login-link")
    public ResponseEntity<CommonResponse<String>> getKakaoLoginUrl() {
        String kakaoLoginUrl = userService.generateKakaoLoginUrl();
        return ResponseEntity.ok(new CommonResponse<>("카카오 로그인 URL", 200, kakaoLoginUrl));
    }

    @Operation(summary = "로그인 중복확인")
    @PreAuthorize("permitAll()")
    @GetMapping("/check-email")
    public ResponseEntity<CommonResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean result = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(new CommonResponse<>("로그인 중복 방지 확인", 200, result));
    }

    @Operation(summary = "회원탈퇴")
    @PreAuthorize("#userId == principal.userId")
    @DeleteMapping("/withdraw")
    public ResponseEntity<CommonResponse<Boolean>> withdraw(@RequestHeader Long userId) {
        userService.withdraw(userId);
        CommonResponse response = new CommonResponse("회원탈퇴가 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
