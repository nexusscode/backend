package org.nexusscode.backend.application.controller;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.service.SaraminService;
import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saramin")
public class SaraminController {
    private final SaraminService saraminService;

    @GetMapping
    public ResponseEntity<CommonResponse> getSaraminContents(@RequestParam String keyword){
        saraminService.getSaraminContents(keyword);
        CommonResponse response = new CommonResponse("사암인 채용공고 조회가 완료되었습니다.",200,"");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
