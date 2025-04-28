package org.nexusscode.backend.application.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SaraminService {

        @Value("saramin.access-key")
        private static String ACCESS_KEY;

        @Value("saramin.endpoint")
        private static String API_URL;

        public String getSaraminContents(String keyword) {
            try {
                String apiURL = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("access-key", ACCESS_KEY)
                    .queryParam("keyword", keyword)
                    .toUriString();

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

                return response.toString();

            } catch (Exception e) {
                throw new RuntimeException("사람인 API 호출 실패", e);
            }
        }
}
