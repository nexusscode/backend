package org.nexusscode.backend;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.nexusscode.backend.interview.client.AwsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest
@TestPropertySource(properties = {
        "MYSQL_DATABASE=interview_platform",
        "MYSQL_USER=interview_user",
        "MYSQL_PASSWORD=secure_password123",
        "AWS_PROFILE=s3-test"
})
@Log4j2
public class AwsSTTClientTest {

    @Autowired
    private AwsClient awsClient;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry) {
        System.setProperty("aws.profile", "s3-test");
    }

    @Test
    void convertAudioTextTest() {
        // given
        String fileUrl = "https://demo-my-testbucket-277707098184.s3.ap-northeast-2.amazonaws.com/upload/audio/example.mp4";

        // when
        Map<String, Object> result = awsClient.convertAudioText(fileUrl);

        // then
        log.info("변환된 텍스트: {}", result.get("transcript"));
        log.info("오디오 길이(초): {}", result.get("duration"));
    }
}
