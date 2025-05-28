package org.nexusscode.backend.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.application.dto.ApplicationRequestDto;
import org.nexusscode.backend.application.dto.ApplicationResponseDto;
import org.nexusscode.backend.application.dto.ApplicationSimpleDto;
import org.nexusscode.backend.application.dto.MemoRequestDto;
import org.nexusscode.backend.application.service.ApplicationService;
import org.nexusscode.backend.global.common.CommonResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application API", description = "공고 관련 API")
@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @Operation(summary = "공고 생성")
    @PreAuthorize("#userId == principal.userId")
    @PostMapping
    public ResponseEntity<CommonResponse<ApplicationResponseDto>> createApplication(@RequestHeader Long userId, @RequestBody ApplicationRequestDto applicationRequestDto){
        ApplicationResponseDto responseDto = applicationService.createApplication(userId,applicationRequestDto);
        CommonResponse<ApplicationResponseDto> response = new CommonResponse<>("공고 생성이 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "단건 공고 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/{applicationId}")
    public ResponseEntity<CommonResponse<ApplicationResponseDto>> getApplication(@RequestHeader Long userId,@PathVariable(name = "applicationId")Long applicationId){
        ApplicationResponseDto responseDto = applicationService.getApplication(userId,applicationId);
        CommonResponse<ApplicationResponseDto> response = new CommonResponse<>("공고 단건 조회가 완료되었습니다.",200,responseDto);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "공고 삭제")
    @PreAuthorize("#userId == principal.userId")
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<CommonResponse> deleteApplication(@RequestHeader Long userId,@PathVariable(name = "applicationId")Long applicationId){
        applicationService.deleteApplication(userId,applicationId);
        CommonResponse response = new CommonResponse<>("공고 삭제가 완료되었습니다.",200,"");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "전체 공고 조회")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping
    public ResponseEntity<CommonResponse<Page<ApplicationSimpleDto>>> getAllApplication(@RequestHeader Long userId,@RequestParam(defaultValue = "1")int page,@RequestParam(defaultValue = "10")int size){
        Page<ApplicationSimpleDto> responseDtoList = applicationService.getAllApplication(userId,page-1,size);
        CommonResponse<Page<ApplicationSimpleDto>> response = new CommonResponse<>("공고 전체 조회가 완료되었습니다.",200,responseDtoList);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /*@Operation(summary = "상세 공고 이미지 ocr 업로드")
    @PostMapping("/{applicationId}/detail")
    public ResponseEntity<CommonResponse> uploadDetailImage(@PathVariable(name = "applicationId") Long applicationId,@RequestParam("file")MultipartFile file) {
        String imageText = applicationService.uploadDetailImage(applicationId,file);
        CommonResponse response = new CommonResponse("상세 공고 이미지 업로드가 완료되었습니다.",200,imageText);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }*/

    @Operation(summary = "공고 검색")
    @PreAuthorize("#userId == principal.userId")
    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<ApplicationSimpleDto>>> searchApplication(@RequestHeader Long userId,@RequestParam(defaultValue = "1")int page,@RequestParam(defaultValue = "10")int size,@RequestParam(name = "searchWord")String searchWord){
        Page<ApplicationSimpleDto> responseDtoList = applicationService.searchApplication(userId,page-1,size,searchWord);
        CommonResponse<Page<ApplicationSimpleDto>> response = new CommonResponse<>("공고 검색이 완료되었습니다.",200,responseDtoList);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "공고 메모 업데이트")
    @PreAuthorize("#userId == principal.userId")
    @PutMapping("/{applicationId}/memo")
    public ResponseEntity<CommonResponse> updateMemo(@RequestHeader Long userId,@PathVariable(name = "applicationId")Long applicationId,@RequestBody MemoRequestDto memoRequestDto){
        applicationService.updateMemo(userId,applicationId,memoRequestDto);
        CommonResponse response = new CommonResponse("공고 메모 업데이트가 완료되었습니다.",200,"");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
