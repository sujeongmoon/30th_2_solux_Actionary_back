package com.req2res.actionarybe.domain.member.entity;

import com.req2res.actionarybe.global.Timestamped;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@Entity
@Table(name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_login_id", columnNames = "loginId"),
                @UniqueConstraint(name = "uk_member_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_member_nickname", columnNames = "nickname")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=20)
    private String loginId;

    @Column(nullable=false)
    private String password;  // BCrypt hash

    @Column(nullable=false, length=50)
    private String email;

    @Column(nullable=false, length=20)
    private String name;

    @Column(length=20)
    private String nickname;

    @Column
    private String signoutName;

    @Column(nullable=false)
    private LocalDate birthday;

    @Column(nullable=false, length=20)
    private String phoneNumber;

    private String profileImageUrl;

    // TimeStamped에서 createdAt, updatedAt 자동으로 넣어줌~
//    @CreatedDate @Column(updatable = false)
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false) // FK 컬럼명 (DB에는 badge_id로 연결됨)
    private Badge badge; // member.getBadge().getId() (O) / member.getBadgeId() (X)

    @Column(nullable = false)
    private boolean withdrawn;

    @Builder
    public Member(String loginId, String password, String name, String email,
                  String phoneNumber, LocalDate birthday, String profileImageUrl, String nickname, Badge badge) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.profileImageUrl = (profileImageUrl == null || profileImageUrl.isBlank())
                ? "https://actionary-s3-bucket-v2.s3.ap-northeast-2.amazonaws.com/static/default_profile/default_profile2.png" : profileImageUrl;
        this.nickname = (nickname == null || nickname.isBlank())
                ? generateDefaultNickname() : nickname;
        this.signoutName = nickname;
        this.badge = badge;
        this.withdrawn = false;
        // createdAt, updatedAt은 Timestamped에서 자동으로 위에 필드 변수에 넣어줄거라, 외부에서 주입받을 필요X
    }

    private String generateDefaultNickname() {
        return "user" + System.currentTimeMillis();
    }
}
