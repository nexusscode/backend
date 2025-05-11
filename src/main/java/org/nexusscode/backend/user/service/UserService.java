package org.nexusscode.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.user.domain.MemberRole;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.dto.UserRequestDto;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void signup(UserRequestDto userRequestDto) {
        User user = User.builder()
            .email(userRequestDto.getEmail())
            .password(userRequestDto.getPassword())
            .name(userRequestDto.getName())
            .role(MemberRole.USER)
            .build();
        userRepository.save(user);
    }
}
