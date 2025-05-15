package org.nexusscode.backend.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.nexusscode.backend.global.Timestamped;

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

    @Column(nullable = false)
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
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
}
