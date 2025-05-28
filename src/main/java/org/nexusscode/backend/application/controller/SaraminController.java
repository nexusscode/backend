package org.nexusscode.backend.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.dto.SaraminResponseDto;
import org.nexusscode.backend.application.service.SaraminService;
import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Saramin API", description = "사람인 공고 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/saramin")
public class SaraminController {
    private final SaraminService saraminService;

    @Operation(summary = "사람인 공고 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping
    public ResponseEntity<CommonResponse<List<SaraminResponseDto>>> getJobsByKeyword(@RequestHeader Long userId, @RequestParam String keyword){
        List<SaraminResponseDto> responseDtos = saraminService.getJobsByKeyword(keyword);
        CommonResponse<List<SaraminResponseDto>> response = new CommonResponse<>("특정 회사에 대한 사람인 채용공고 리스트 조회가 완료되었습니다.",200,responseDtos);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
