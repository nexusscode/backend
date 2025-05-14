package org.nexusscode.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.user.domain.MemberRole;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void signup(UserRequestDto userRequestDto) {
        User user = User.builder()
            .email(userRequestDto.getEmail())
            .password(userRequestDto.getPassword())
            .name(userRequestDto.getName())
            .userRoleList(List.of(MemberRole.USER))
            .build();
        userRepository.save(user);
    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }
}
