package org.nexusscode.backend.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;
import org.nexusscode.backend.user.dto.ProfileUpdateRequestDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "dev_type")
    private DevType devType;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<MemberRole> userRoleList = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, List<MemberRole> userRoleList, boolean isSocial) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.userRoleList = userRoleList;
        this.social = isSocial;
    }

    public void change(String password, String name) {
        this.password = password;
        this.name = name;
    }

    public void addUserRole(MemberRole memberRole) {
        userRoleList.add(memberRole);
    }
  
    @Builder
    public User(String email, String password, String name, MemberRole role,String phoneNumber,DevType devType, ExperienceLevel experienceLevel) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.phoneNumber=phoneNumber;
        this.devType=devType;
        this.experienceLevel=experienceLevel;
    }

    public void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        this.name=profileUpdateRequestDto.getName();
        this.phoneNumber=profileUpdateRequestDto.getPhoneNumber();
        this.devType=DevType.from(profileUpdateRequestDto.getDevType());
        this.experienceLevel=ExperienceLevel.from(profileUpdateRequestDto.getExperienceLevel());
    }
}
