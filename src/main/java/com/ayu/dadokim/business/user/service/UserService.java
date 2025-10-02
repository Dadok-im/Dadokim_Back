package com.ayu.dadokim.business.user.service;

import com.ayu.dadokim.business.user.form.Enum.UserRoleType;
import com.ayu.dadokim.business.user.form.UserEntity;
import com.ayu.dadokim.business.user.form.request.UserRequest;
import com.ayu.dadokim.business.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 1. 자체 로그인 회원가입 (회원 존재 여부 확인)
     */
    @Transactional(readOnly = true) // 트랜잭션 진행 시, 테이블 수정 없이 오로지 조회(readOnly) 가 목적임을 명시한다.
    public Boolean existUser(UserRequest request) {
        return userRepository.existsByUsername(request.getUsername());
    }

    /**
     * 2. 자체 로그인 회원가입 진행
     */
    @Transactional
    public Long addUser(UserRequest request) {

        // 1. 중복 유저 검사 진행
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("해당 유저가 이미 존재합니다.");
        }
        // 2. 중복 유저가 아닐 경우, 회원 정보 값 entity 설정
        UserEntity userEntity = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isLock(false)
                .isSocial(false)
                .roleType(UserRoleType.USER)
                .nickname(request.getNickname())
                .email(request.getEmail())
                .build();

        return userRepository.save(userEntity).getId();
    }

    /**
     * 3. 자체 로그인
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByUsernameAndIsLockAndIsSocial(username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles(userEntity.getRoleType().name())
                .accountLocked(userEntity.getIsLock())
                .build();
    }

    /**
     * 4. 자체 로그인 회원 정보 수정 -> 로그인이 되어있는 경우 과연 인증이 필요할까?
     */
    @Transactional
    public Long updateUser(UserRequest request) throws AccessDeniedException {
        // 1. 현재 스레드에 로그인되어 있는 사용자의 이름을 가져온다.
        String sessionUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        // 2. 사용자의 이름이 같지 않다면, exception 을 던진다.
        if (!sessionUsername.equals(request.getUsername())) {
            throw new AccessDeniedException("본인의 계정만 수정이 가능합니다.");
        }
        // 3. 회원 조회
        UserEntity userEntity = userRepository.findByUsernameAndIsLockAndIsSocial(request.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(request.getUsername()));
        // 4. 회원 정보 수정
        userEntity.updateUser(request);

        return userRepository.save(userEntity).getId();

    }

    /**
     *  5. 소셜 로그인 (로그인 시 : 신규 회원 = 가입, 기존 회원 = 업데이트)
     */

    /**
     *  6. 자체 / 소셜 로그인 회원 정보 조회
     */

    /**
     *  7. 자체/소셜 로그인 회원 탈퇴
     */
}
