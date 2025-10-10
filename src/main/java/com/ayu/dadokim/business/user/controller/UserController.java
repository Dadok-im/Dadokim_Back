package com.ayu.dadokim.business.user.controller;

import com.ayu.dadokim.business.user.form.request.UserRequest;
import com.ayu.dadokim.business.user.form.response.UserResponse;
import com.ayu.dadokim.business.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user") // ✅ 공통 prefix 적용
public class UserController {

    private final UserService userService;

    // 자체 로그인 유저 존재 확인
    @PostMapping(value = "/exist", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> existUserApi(
            @Validated(UserRequest.existGroup.class) @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.existUser(request));
    }

    // 회원가입
    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Long>> joinApi(
            @Validated(UserRequest.addGroup.class) @RequestBody UserRequest request) {

        Long id = userService.addUser(request);
        Map<String, Long> responseBody = Collections.singletonMap("userEntityId", id);

        return ResponseEntity.status(201).body(responseBody);
    }

    // 유저 정보
    //@GetMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    // -> 회원 정보 api 호출 시에 요청 헤더에 Content-Type 헤더를 같이 보내면 안된다.
    @GetMapping("/me")
    public ResponseEntity<UserResponse> userMeApi() {
        return ResponseEntity.ok(userService.readUser());
    }

    // 유저 수정 (자체 로그인 유저만)
    @PutMapping("/put")
    public ResponseEntity<Long> updateUserApi(
            @Validated(UserRequest.updateGroup.class) @RequestBody UserRequest request) throws AccessDeniedException {
        return ResponseEntity.ok(userService.updateUser(request));
    }

    // 유저 제거 (자체/소셜)
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserApi(
            @Validated(UserRequest.deleteGroup.class) @RequestBody UserRequest request) throws AccessDeniedException {
        userService.deleteUser(request);
        return ResponseEntity.noContent().build(); // ✅ 삭제 성공 시 204 No Content
    }
}

