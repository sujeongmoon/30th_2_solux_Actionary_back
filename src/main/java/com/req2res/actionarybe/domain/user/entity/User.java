package com.req2res.actionarybe.domain.user.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "user",
        indexes = { @Index(name="idx_user_login_id", columnList="loginId", unique = true),
                @Index(name="idx_user_email", columnList="email", unique = true) })
//@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=20)
    private String loginId;

    @Column(nullable=false)
    private String password;          // BCrypt hash

    @Column(nullable=false, unique=true, length=50)
    private String email;

    @Column(nullable=false, length=20)
    private String name;

    @Column(unique = true, length=20)
    private String nickname;

    @Column(nullable=false)
    private LocalDate birthday;

    @Column(nullable=false, length=20)
    private String phoneNumber;

    private String imageUrl;

//    @CreatedDate @Column(updatable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    private LocalDateTime updatedAt;

    // 나중에 BadgeId FK 연결하기
    private Long badgeId;

    @Builder
    public User(String loginId, String password, String name, String email,
                String phoneNumber, LocalDate birthday, String imageUrl, String nickname, Long badgeId) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.imageUrl = (imageUrl == null || imageUrl.isBlank())
                ? "http://.../default_profile.png" : imageUrl;
        this.nickname = (nickname == null || nickname.isBlank())
                ? generateDefaultNickname() : nickname;
        this.badgeId=(nickname == null || nickname.isBlank())
                ? 0:badgeId;
        // createdAt, updatedAt은 Timestamped에서 자동으로 위에 필드 변수에 넣어줄거라, 외부에서 주입받을 필요X
    }

    private String generateDefaultNickname() {
        return "user" + System.currentTimeMillis();
    }
}
