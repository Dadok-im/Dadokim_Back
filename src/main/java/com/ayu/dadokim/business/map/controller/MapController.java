package com.ayu.dadokim.business.map.controller;
import com.ayu.dadokim.business.map.dto.MapDto;
import com.ayu.dadokim.business.map.service.MapService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@Validated
@RequiredArgsConstructor
public class  MapController {

    private final MapService mapService;

    @GetMapping("/search")
    public ResponseEntity<List<MapDto>> search(
            @RequestParam(name = "lat") @NotNull Double lat,
            @RequestParam(name = "lng") @NotNull Double lng,
            @RequestParam(name = "q", defaultValue = "정신건강의학과") String query,
            @RequestParam(name = "page", defaultValue = "1") @Min(1) @Max(45) Integer page,
            @RequestParam(name = "size", defaultValue = "15") @Min(1) @Max(15) Integer size,
            @RequestParam(name = "radius", required = false) Integer radius
    ) {
        var list = mapService.search(query, lat, lng, radius, page, size);
        return ResponseEntity.ok(list);
    }
}
