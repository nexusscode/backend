package org.nexusscode.backend.application.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nexusscode.backend.application.dto.SaraminResponseDto;
import org.nexusscode.backend.global.exception.CustomException;
import org.nexusscode.backend.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class SaraminService {

    @Value("${saramin.access-key}")
    private String accessKey;

    @Value("${saramin.endpoint}")
    private String apiUrl;

    public List<SaraminResponseDto> getJobsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("검색 키워드는 비어 있을 수 없습니다.");
        }
        String broadKeyword = keyword.length() >= 2 ? keyword.substring(0, 2) : keyword;
        //log.info("[사람인 검색] 요청 keyword: '{}', broadKeyword:'{}'", keyword,broadKeyword);

        try {
            String apiURL = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access-key", accessKey)
                .queryParam("keywords", keyword)
                .toUriString();

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            BufferedReader br = (responseCode == 200)
                ? new BufferedReader(new InputStreamReader(con.getInputStream()))
                : new BufferedReader(new InputStreamReader(con.getErrorStream()));

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            String rawJson = response.toString();
            JSONObject json = new JSONObject(rawJson);

            if (json.has("code") && json.getInt("code") == 4) {
                String message = json.optString("message", "알 수 없는 오류");
                log.error("사람인 API 오류 발생 - 코드: {}, 메시지: {}", 4, message);
                throw new CustomException(ErrorCode.TOO_MANY_REQUESTS);
            }

            return filterJobsByCompanyName(response.toString(), keyword);

        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
            log.error("사람인 API 호출 실패: {}", e.getMessage());
            return List.of();
        }
    }

    public List<SaraminResponseDto> filterJobsByCompanyName(String jsonResponse, String keyword) {
        List<SaraminResponseDto> filteredJobs = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonResponse);

        if (!jsonObject.has("jobs")) {
            return filteredJobs;
        }

        JSONArray jobs = jsonObject.getJSONObject("jobs").optJSONArray("job");

        if (jobs == null) {
            return filteredJobs;
        }

        for (int i = 0; i < jobs.length(); i++) {
            JSONObject job = jobs.getJSONObject(i);

            JSONObject companyObject = job.optJSONObject("company");
            if (companyObject == null) {
                continue;
            }

            JSONObject detailObject = companyObject.optJSONObject("detail");
            if (detailObject == null) {
                continue;
            }

            String companyName = detailObject.optString("name", "");
            String normalizedCompany = companyName.replaceAll("[^가-힣a-zA-Z0-9]", "").toLowerCase();
            String normalizedTarget = keyword.replaceAll("[^가-힣a-zA-Z0-9]", "").toLowerCase();

            if (normalizedCompany.contains(normalizedTarget)) {
                String id = job.optString("id", null);
                String title = job.getJSONObject("position").optString("title", "제목 없음");
                String expirationTimestampStr = job.optString("expiration-timestamp", null);

                ZonedDateTime kstTime = null;
                if (expirationTimestampStr != null) {
                    try {
                        long expirationEpoch = Long.parseLong(expirationTimestampStr);
                        kstTime = Instant.ofEpochSecond(expirationEpoch).atZone(ZoneId.of("Asia/Seoul"));
                    } catch (NumberFormatException e) {
                        kstTime = null;
                    }
                }

                String experienceLevel = job.getJSONObject("position")
                    .getJSONObject("experience-level")
                    .optString("name", "경력 정보 없음");

                filteredJobs.add(
                    new SaraminResponseDto(
                        id,
                        companyName,
                        title,
                        kstTime != null ? kstTime.toLocalDateTime() : null,
                        experienceLevel
                    )
                );
            }

        }

        return filteredJobs;
    }

}
