package org.nexusscode.backend.resume.service;

import lombok.RequiredArgsConstructor;
import org.nexusscode.backend.resume.repository.ResumeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
}
