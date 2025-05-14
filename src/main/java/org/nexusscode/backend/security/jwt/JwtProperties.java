package org.nexusscode.backend.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expire-minutes}")
    private int accessExpireMin;

    @Value("${jwt.refresh-token-expire-minutes}")
    private int refreshExpireMin;

}