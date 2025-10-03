package com.ayu.dadokim.business.map.service;

import com.ayu.dadokim.business.map.dto.MapDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapService {
    private final KakaoLocalClient kakao;

    public List<MapDto> search(String query, double lat, double lng, Integer radius, int page, int size) {
        var res = kakao.searchKeyword(query, lat, lng, radius, page, size);
        if (res == null || res.getDocuments() == null) return List.of();
        return Arrays.stream(res.getDocuments()).map(d -> new MapDto(
                d.getId(),
                d.getPlace_name(),
                d.getAddress_name(),
                d.getRoad_address_name(),
                d.getPhone(),
                parseDbl(d.getY()),
                parseDbl(d.getX()),
                d.getPlace_url(),
                parseIntSafe(d.getDistance())
        )).collect(Collectors.toList());
    }

    private double parseDbl(String s){ try { return Double.parseDouble(s); } catch(Exception e){ return 0d; } }
    private Integer parseIntSafe(String s){ try { return s==null? null : Integer.parseInt(s); } catch(Exception e){ return null; } }
}
