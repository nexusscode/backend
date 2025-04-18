package org.nexusscode.backend.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.dto.ApplicationRequestDto;
import org.nexusscode.backend.application.dto.ApplicationResponseDto;
import org.nexusscode.backend.application.dto.ApplicationUpdateRequestDto;
import org.nexusscode.backend.application.service.ApplicationService;
import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application API", description = "공고 관련 API")
@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @Operation(summary = "공고 생성")
    @PostMapping
    public ResponseEntity<CommonResponse<ApplicationResponseDto>> createApplication(@RequestBody ApplicationRequestDto applicationRequestDto){
        ApplicationResponseDto responseDto = applicationService.createApplication(applicationRequestDto);
        CommonResponse response = new CommonResponse("공고 생성이 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "공고 수정")
    @PutMapping("/{applicationId}")
    public ResponseEntity<CommonResponse<ApplicationResponseDto>> updateApplication(@RequestBody ApplicationUpdateRequestDto updateRequestDto,@PathVariable(name = "applicationId")Long applicationId){
        ApplicationResponseDto responseDto = applicationService.updateApplication(updateRequestDto,applicationId);
        CommonResponse response = new CommonResponse("공고 수정이 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "단건 공고 조회")
    @GetMapping("/{applicationId}")
    public ResponseEntity<CommonResponse<ApplicationResponseDto>> getApplication(@PathVariable(name = "applicationId")Long applicationId){
        ApplicationResponseDto responseDto = applicationService.getApplication(applicationId);
        CommonResponse response = new CommonResponse("공고 단건 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "공고 삭제")
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<CommonResponse> deleteApplication(@PathVariable(name = "applicationId")Long applicationId){
        applicationService.deleteApplication(applicationId);
        CommonResponse response = new CommonResponse("공고 삭제가 완료되었습니다.",200,"");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "전체 공고 조회")
    @GetMapping
    public ResponseEntity<CommonResponse<List<ApplicationResponseDto>>> getAllApplication(){
        List<ApplicationResponseDto> responseDtoList = applicationService.getAllApplication();
        CommonResponse response = new CommonResponse("공고 전체 조회가 완료되었습니다.",200,responseDtoList);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
