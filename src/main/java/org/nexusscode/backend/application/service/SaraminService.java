package org.nexusscode.backend.application.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nexusscode.backend.application.dto.SaraminResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SaraminService {

    @Value("${saramin.access-key}")
    private String accessKey;

    @Value("${saramin.endpoint}")
    private String apiUrl;

    public List<SaraminResponseDto> getSaraminContents(String keyword) {
        try {
            System.out.println("keyword : " + keyword);
            String apiURL = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("access-key", accessKey)
                .queryParam("keywords", keyword)
                .toUriString();
            System.out.println("apiUrl : " + apiURL);

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int responseCode = con.getResponseCode();
            BufferedReader br = (responseCode == 200) ?
                new BufferedReader(new InputStreamReader(con.getInputStream())) :
                new BufferedReader(new InputStreamReader(con.getErrorStream()));

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            List<SaraminResponseDto> responseDtos = filterJobsByCompanyName(response.toString(),
                keyword);
            return responseDtos;

        } catch (Exception e) {
            throw new RuntimeException("사람인 API 호출 실패", e);
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

            if (companyName.toLowerCase().contains(keyword.toLowerCase())) {
                String id = job.optString("id", null);
                String title = job.getJSONObject("position").optString("title", "제목 없음");
                filteredJobs.add(new SaraminResponseDto(id, companyName, title));
            }
        }

        return filteredJobs;
    }
}
