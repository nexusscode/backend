package org.nexusscode.backend.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.user.domain.MemberRole;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.dto.*;
import org.nexusscode.backend.user.repository.UserRepository;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserRequestDto userRequestDto) {
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(userRequestDto.getPassword())
                .name(userRequestDto.getName())
                .build();

        user.addUserRole(MemberRole.USER);

        userRepository.save(user);
    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(
            ()->new CustomException(ErrorCode.NOT_FOUND_USER)
        );
    }

    public void modifyUser(UserModifyDTO userModifyDTO) {
        User user = userRepository.findByEmail(userModifyDTO.getEmail()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        user.change(userModifyDTO.getName(), userModifyDTO.getPassword());
    }

    public UserDTO getKakaoMember(String accessToken) {
        KakaoAccount kakaoAccount = getEmailFromKakaoAccessToken(accessToken);
        String email = kakaoAccount.getEmail();

        log.info("email: " + email);

        Optional<User> result = userRepository.findByEmail(email);

        if(result.isPresent()) {
            UserDTO userDTO = entityToDTO(result.get());

            return userDTO;
        }

        User socialMember = makeSocialUser(email, kakaoAccount.getProfile().getNickname());

        userRepository.save(socialMember);

        UserDTO userDTO = entityToDTO(socialMember);

        return userDTO;
    }

    private User makeSocialUser(String email, String nickname) {
        String tempPassword = makeTempPassword();

        log.info("tempPassword: " + tempPassword);

        String name = nickname.isEmpty() ? "소셜회원" : nickname;

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .name(name)
                .social(true)
                .build();

        user.addUserRole(MemberRole.USER);

        return user;
    }

    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }

        return buffer.toString();
    }

    public UserDTO entityToDTO(User member) {
        UserDTO dto = new UserDTO(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.isSocial(),
                member.getUserRoleList().stream().map(userRole -> userRole.name()).collect(Collectors.toList())
        );
        return dto;
    }

    public KakaoAccount getEmailFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if (accessToken == null || accessToken.isBlank()) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response = new RestTemplate().exchange(
                kakaoGetUserURL,
                HttpMethod.GET,
                entity,
                KakaoUserResponse.class
        );

        KakaoUserResponse userResponse = response.getBody();

        if (userResponse == null || userResponse.getKakao_account() == null) {
            throw new CustomException(ErrorCode.KAKAO_USER_FETCH_FAILED);
        }

        KakaoAccount account = userResponse.getKakao_account();

        if (!Boolean.TRUE.equals(account.getHas_email()) ||
                !Boolean.TRUE.equals(account.getIs_email_valid()) ||
                !Boolean.TRUE.equals(account.getIs_email_verified()) ||
                account.getEmail() == null) {
            throw new CustomException(ErrorCode.EMAIL_NOT_PROVIDED);
        }

        return account;
    }

}
