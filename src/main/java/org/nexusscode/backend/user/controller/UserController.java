package org.nexusscode.backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.security.dto.TokenResponseDTO;
import org.nexusscode.backend.security.jwt.JWTProvider;
import org.nexusscode.backend.security.repository.RedisRefreshTokenRepository;
import org.nexusscode.backend.user.dto.UserDTO;
import org.nexusscode.backend.user.dto.UserModifyDTO;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Log4j2
public class UserController {

    private final UserService userService;
    private final JWTProvider jwtProvider;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

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
        redisRefreshTokenRepository.saveRefreshToken(userId, newRefreshToken, Duration.ofDays(1));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(1))
                .build();

        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(newAccessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new CommonResponse<>("유저 정보 수정 및 토큰 재발급 완료", 200, tokenResponseDTO));
    }

    @Operation(summary = "kakao 로그인")
    @PreAuthorize("permitAll()")
    @GetMapping("/kakao")
    public ResponseEntity<CommonResponse<TokenResponseDTO>> getMemberFromKakao(@RequestParam("accessToken") String accessToken) {
        log.info("access Token: {}", accessToken);

        UserDTO memberDTO = userService.getKakaoMember(accessToken);

        Map<String, Object> claims = memberDTO.getClaims();

        String jwtAccessToken = jwtProvider.generateAccessToken(claims);
        String jwtRefreshToken = jwtProvider.generateRefreshToken(claims);

        String userId = (String) claims.get("userId");
        redisRefreshTokenRepository.saveRefreshToken(userId, jwtRefreshToken, Duration.ofDays(1));

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwtRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(1))
                .build();

        TokenResponseDTO responseDTO = new TokenResponseDTO(accessToken);

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

        String userId = (String) claims.get("userId");

        redisRefreshTokenRepository.deleteRefreshToken(userId);

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

}
