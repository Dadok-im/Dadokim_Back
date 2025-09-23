package com.ayu.dadokim.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/test")
public class TestController {

    @GetMapping("/user")
    public ResponseEntity<Object> testApi() {
        String result = "기본 API 통신에 성공하였습니다.";
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
