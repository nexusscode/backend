package org.nexusscode.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.user.domain.DevType;
import org.nexusscode.backend.user.domain.ExperienceLevel;
import org.nexusscode.backend.user.domain.MemberRole;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.dto.EmailFindRequestDto;
import org.nexusscode.backend.user.dto.PasswordFindRequestDto;
import org.nexusscode.backend.user.dto.ProfileResponseDto;
import org.nexusscode.backend.user.dto.ProfileUpdateRequestDto;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void signup(UserRequestDto userRequestDto) {
        DevType devType = DevType.from(userRequestDto.getDevType());
        ExperienceLevel experienceLevel = ExperienceLevel.from(userRequestDto.getExperienceLevel());

        System.out.println(devType);
        System.out.println(experienceLevel);
        User user = User.builder()
            .email(userRequestDto.getEmail())
            .password(userRequestDto.getPassword())
            .name(userRequestDto.getName())
            .phoneNumber(userRequestDto.getPhoneNumber())
            .devType(devType)
            .experienceLevel(experienceLevel)
            .role(MemberRole.USER)
            .build();
        userRepository.save(user);
    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    public String findEmail(EmailFindRequestDto emailFindRequestDto) {
        User user = userRepository.findByNameAndPhoneNumber(emailFindRequestDto.getName(),emailFindRequestDto.getPhoneNumber());
        if(user==null){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        return user.getEmail();
    }

    public String findPassword(PasswordFindRequestDto passwordFindRequestDto) {
        User user = userRepository.findByEmailAndNameAndPhoneNumber(passwordFindRequestDto.getEmail(),passwordFindRequestDto.getName(),passwordFindRequestDto.getPhoneNumber());
        if(user==null){
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        return user.getPassword();
    }

    public ProfileResponseDto getProfile(Long userId) {
        //로그인 유저 프로필 조회
        User user = findById(userId);
        return new ProfileResponseDto(user);

    }

    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileUpdateRequestDto profileUpdateRequestDto) {
        //로그인 유저 프로필 조회
        User user = findById(userId);
        user.updateProfile(profileUpdateRequestDto);
        userRepository.save(user);
        return new ProfileResponseDto(user);
    }
}
