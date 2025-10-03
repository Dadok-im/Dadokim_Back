package com.ayu.dadokim.business.map.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapDto {
    private String id;
    private String name;
    private String address;
    private String roadAddress;
    private String phone;
    private double lat;
    private double lng;
    private String placeUrl;
    private Integer distance; // meter
}
