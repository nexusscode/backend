package org.nexusscode.backend.user.controller;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.common.CommonResponse;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody UserRequestDto userRequestDto){
        userService.signup(userRequestDto);
        CommonResponse response = new CommonResponse("회원가입이 완료되었습니다.",200,"");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
