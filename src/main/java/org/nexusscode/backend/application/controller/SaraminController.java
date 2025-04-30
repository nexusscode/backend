package org.nexusscode.backend.application.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.dto.ApplicationResponseDto;
import org.nexusscode.backend.application.dto.SaraminResponseDto;
import org.nexusscode.backend.application.service.SaraminService;
import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saramin")
public class SaraminController {
    private final SaraminService saraminService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<SaraminResponseDto>>> getJobsByKeyword(@RequestParam String keyword){
        List<SaraminResponseDto> responseDtos = saraminService.getJobsByKeyword(keyword);
        CommonResponse<List<SaraminResponseDto>> response = new CommonResponse("특정 회사에 대한 사람인 채용공고 리스트 조회가 완료되었습니다.",200,responseDtos);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
