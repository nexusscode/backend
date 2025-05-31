package org.nexusscode.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.nexusscode.backend.user.domain.User;
import org.nexusscode.backend.user.domain.UserStat;
import org.nexusscode.backend.user.repository.UserRepository;
import org.nexusscode.backend.user.repository.UserStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserStatService {
    private final UserStatRepository userStatRepository;
    private final UserRepository userRepository;

    public UserStat createUserStat(User user) {
        UserStat stat = UserStat.builder()
                .user(user)
                .totalInterviews(0)
                .build();

        return userStatRepository.save(stat);
    }

    public UserStat createUserStatByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserStat stat = UserStat.builder()
                .user(user)
                .totalInterviews(0)
                .build();

        return userStatRepository.save(stat);
    }

    @Transactional(readOnly = true)
    public UserStat getUserStat(Long userId) {
        return userStatRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }

    public UserStat getUserStatAndIncrement(Long userId) {
        UserStat result = userStatRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        result.increaseInterviewCount();

        return result;
    }


    public void incrementInterviewCount(Long userId) {
        UserStat stat = userStatRepository.findById(userId)
                .orElseGet(() -> createUserStatByUserId(userId));

        stat.increaseInterviewCount();
    }

    public void deleteUserStat(Long userId) {
        userStatRepository.deleteById(userId);
    }
}
