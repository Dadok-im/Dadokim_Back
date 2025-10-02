package com.ayu.dadokim.business.user.form;


import com.ayu.dadokim.business.user.form.Enum.SocialProviderType;
import com.ayu.dadokim.business.user.form.Enum.UserRoleType;
import com.ayu.dadokim.business.user.form.request.UserRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 생성일, 수정일 컬럼에 대해 자동으로 값을 넣어주는 AuditingEntityListener 를 추가하였습니다.
 **/

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_lock", nullable = false)
    private Boolean isLock;

     // 자체 로그인 인지 소셜 로그인 인지
    @Column(name = "is_social", nullable = false)
    private Boolean isSocial;

    // 소셜 로그인 종류 (naver or kakao)
    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    // User or Admin
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private UserRoleType roleType;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public void updateUser(UserRequest request) {
        this.email = request.getEmail();
        this.nickname = request.getNickname();
    }
}
