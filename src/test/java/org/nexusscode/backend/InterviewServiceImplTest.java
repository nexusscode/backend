package org.nexusscode.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nexusscode.backend.interview.client.AwsClient;
import org.nexusscode.backend.interview.client.support.GptVoice;
import org.nexusscode.backend.interview.domain.InterviewAnswer;
import org.nexusscode.backend.interview.domain.InterviewQuestion;
import org.nexusscode.backend.interview.domain.InterviewSession;
import org.nexusscode.backend.interview.domain.InterviewType;
import org.nexusscode.backend.interview.dto.*;
import org.nexusscode.backend.interview.repository.InterviewAnswerRepository;
import org.nexusscode.backend.interview.repository.InterviewQuestionRepository;
import org.nexusscode.backend.interview.repository.InterviewSessionRepository;
import org.nexusscode.backend.interview.repository.InterviewSummaryRepository;
import org.nexusscode.backend.interview.service.InterviewAsyncService;
import org.nexusscode.backend.interview.service.InterviewCacheService;
import org.nexusscode.backend.interview.service.InterviewServiceImpl;
import org.nexusscode.backend.interview.service.GeneratorService;
import org.nexusscode.backend.resume.domain.Resume;
import org.nexusscode.backend.resume.domain.ResumeItem;
import org.nexusscode.backend.resume.repository.ResumeItemRepository;
import org.nexusscode.backend.resume.repository.ResumeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private ResumeItemRepository resumeItemRepository;
    @Mock
    private InterviewQuestionRepository interviewQuestionRepository;
    @Mock
    private InterviewSessionRepository interviewSessionRepository;
    @Mock
    private AwsClient awsClient;
    @Mock
    private InterviewAnswerRepository interviewAnswerRepository;
    @Mock
    private InterviewAsyncService interviewAsyncService;
    @Mock
    private GeneratorService generatorService;
    @Mock
    private InterviewSummaryRepository interviewSummaryRepository;
    @Mock
    private InterviewCacheService interviewCacheService;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    @Test
    void startInterview_success() {
        // given
        InterviewStartRequest request = InterviewStartRequest.builder()
                .resumeId(1L).title("title").interviewType(GptVoice.FABLE)
                .build();
        Resume resume = mock(Resume.class);
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(resumeItemRepository.findByResumeId(1L)).thenReturn(Optional.of(List.of(mock(ResumeItem.class))));

        List<InterviewQuestion> questions = List.of(mock(InterviewQuestion.class));
        //when(questionGenerator.generateQuestionsFromResume(any(), eq(3))).thenReturn(questions);

        InterviewSession session = InterviewSession.builder().id(10L).build();
        when(interviewSessionRepository.save(any())).thenReturn(session);

        // when
        Long sessionId = interviewService.startInterview(request);

        // then
        assertThat(sessionId).isEqualTo(10L);
    }

    @Test
    void getQuestion_cached_success() {
        // given
        Long sessionId = 1L;
        int seq = 0;
        InterviewQuestion question = InterviewQuestion.builder()
                .id(10L)
                .questionText("What is your strength?")
                .intentText("Check personality")
                .seq(0)
                .interviewType(InterviewType.PERSONALITY)
                .build();

        when(interviewCacheService.getCachedQuestionBySeq(sessionId, seq))
                .thenReturn(Optional.of(question));

        // when
        QuestionAndHintDTO result = interviewService.getQuestion(sessionId, seq);

        // then
        assertThat(result.getInterviewQuestionId()).isEqualTo(10L);
        assertThat(result.getQuestionText()).isEqualTo("What is your strength?");
        verifyNoMoreInteractions(interviewQuestionRepository);
    }

    @Test
    void submitAnswer_success_and_generateAdvice() {
        // given
        InterviewAnswerRequest request = InterviewAnswerRequest.builder()
                .questionId(1L)
                .audioUrl("audioUrl")
                .isCheated(false)
                .build();

        InterviewSession session = mock(InterviewSession.class);
        InterviewQuestion question = mock(InterviewQuestion.class);

        // mock 연결
        when(interviewQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(question.getSession()).thenReturn(session);
        when(session.getQuestionCount()).thenReturn(5);
        when(session.isAdditionalQuestion()).thenReturn(false);
        when(question.getSeq()).thenReturn(4); // questionCount - 1 = seq
        when(awsClient.convertAudioText("audioUrl"))
                .thenReturn(Map.of("transcript", "This is my answer", "duration", 10));
        InterviewAnswer savedAnswer = InterviewAnswer.builder().id(100L).build();
        when(interviewAnswerRepository.save(any())).thenReturn(savedAnswer);

        // when
        Long answerId = interviewService.submitAnswer(request);

        // then
        assertThat(answerId).isEqualTo(100L);
    }


    @Test
    void getList_cacheMiss_thenRepositoryLoad() {
        // given
        Long applicationId = 1L;

        List<InterviewSessionDTO> sessionList = List.of(mock(InterviewSessionDTO.class));
        when(interviewSessionRepository.findSessionListByApplicationId(applicationId))
                .thenReturn(Optional.of(sessionList));

        // when
        List<InterviewSessionDTO> result = interviewService.getList(applicationId);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void getFullSessionDetail_cacheHit() {
        // given
        Long sessionId = 1L;
        InterviewAllSessionDTO cachedDTO = mock(InterviewAllSessionDTO.class);

        // when
        InterviewAllSessionDTO result = interviewService.getFullSessionDetail(sessionId);

        // then
        assertThat(result).isEqualTo(cachedDTO);
        verifyNoMoreInteractions(interviewSessionRepository);
    }

    @Test
    void saveSessionToArchive_success() {
        // given
        InterviewSession session = mock(InterviewSession.class);
        when(interviewSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // when
        interviewService.saveSessionToArchive(1L);

        // then
        verify(session).saveSessionToArchive();
    }

    @Test
    void deleteSessionToArchive_success() {
        // given
        InterviewSession session = mock(InterviewSession.class);
        when(interviewSessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // when
        interviewService.deleteSessionToArchive(1L);

        // then
        verify(session).deleteSessionToArchive();
    }
}

