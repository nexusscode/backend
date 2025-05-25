package org.nexusscode.backend.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.dto.UserDTO;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("----------- loadUserByUsername ----------");

        User user = userRepository.getWithRoles(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.isSocial(),
                user.getUserRoleList().stream().map(val -> val.getRole()).collect(Collectors.toList())
        );

        log.info(userDTO);

        return userDTO;
    }
}
