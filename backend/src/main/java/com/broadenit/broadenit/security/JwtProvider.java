package com.broadenit.broadenit.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

@Configuration
public class JwtProvider {

    static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    public static String generateToken(Authentication auth) {
        String jwt = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + JwtConstant.EXPIRATION_TIME))
                .claim("email", auth.getName())
                .signWith(key)
                .compact();
        System.out.println("Token generated: " + jwt);
        return jwt;
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        StringBuilder auths = new StringBuilder();
        for (GrantedAuthority authority : authorities) {
            auths.append(authority.getAuthority()).append(",");
        }
        return auths.toString();
    }

    public static String getEmailFromToken(String jwt){
        jwt = jwt.substring(7);

        try{
            Claims claims = Jwts.parser().
                    setSigningKey(key).
                    build().
                    parseClaimsJws(jwt).
                    getBody();

            String emails = claims.get("email").toString();
            System.out.println("Email from token: " + emails);
            return emails;
        }catch (Exception e){
            System.out.println("Error in getEmailFromToken: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
