package org.nexusscode.backend.user.dto;

import lombok.Getter;
import org.nexusscode.backend.user.domain.MemberRole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class UserDTO extends User {

    private String email;
    private String password;
    private String name;
    private boolean social;
    private List<String> roleNames = new ArrayList<>();

    public UserDTO(String email, String password, String name, boolean social, List<String> roleNames) {
        super(email, password, roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));

        this.email = email;
        this.password = password;
        this.name = name;
        this.social = social;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", email);
        dataMap.put("password", password);
        dataMap.put("name", name);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);

        return dataMap;

    }

}
