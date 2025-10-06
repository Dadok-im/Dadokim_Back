package com.ayu.dadokim.business.map.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class KakaoLocalClient {

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoLocalResponse searchKeyword(String query,
                                            double lat,
                                            double lng,
                                            Integer radius,
                                            int page,
                                            int size) {

        int safePage = Math.max(1, Math.min(page, 45));
        int safeSize = Math.max(1, Math.min(size, 15));

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", query)
                .queryParam("x", Double.toString(lng)) // x=lng
                .queryParam("y", Double.toString(lat)) // y=lat
                .queryParam("sort", "distance")
                .queryParam("page", safePage)
                .queryParam("size", safeSize);

        if (radius != null) {
            int r = Math.max(0, Math.min(radius, 20000));
            if (r > 0) builder.queryParam("radius", r);
        }

        //URI uri = builder.build().encode(StandardCharsets.UTF_8).toUri();
        URI uri = builder.encode().build().toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + restApiKey);
        //headers.setAccept(MediaType.parseMediaTypes("application/json"));
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));


        ResponseEntity<KakaoLocalResponse> resp = restTemplate.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), KakaoLocalResponse.class);

        KakaoLocalResponse body = resp.getBody();
        return body != null ? body : new KakaoLocalResponse();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoLocalResponse {
        private Meta meta;
        private Document[] documents;

        @Data @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Meta {
            private int total_count;
            private int pageable_count;
            private boolean is_end;
        }

        @Data @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Document {
            private String id;
            private String place_name;
            private String address_name;
            private String road_address_name;
            private String phone;
            private String place_url;
            private String x;        // lng
            private String y;        // lat
            private String distance; // meter (string)
        }
    }
}
