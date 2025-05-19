package org.nexusscode.backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.user.dto.EmailFindRequestDto;
import org.nexusscode.backend.user.dto.PasswordFindRequestDto;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API",description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody UserRequestDto userRequestDto){
        userService.signup(userRequestDto);
        CommonResponse response = new CommonResponse("회원가입이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
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


}
