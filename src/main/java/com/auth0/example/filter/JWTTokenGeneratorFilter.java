package com.auth0.example.filter;

import com.auth0.example.constant.SecurityConstant;
import com.auth0.example.entity.Role;
import com.auth0.example.entity.Users;
import com.auth0.example.repository.UserRepository;
import com.auth0.example.service.JwtService;
import io.jsonwebtoken.security.Keys;
import lombok.var;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository repository;
    private PasswordEncoder passwordEncoder;

    public JWTTokenGeneratorFilter(JwtService jwtService, UserRepository repository) {
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            processAuthentication(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void processAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        Object emailObject = oidcUser.getUserInfo().getClaims().get("email");
        String email = (emailObject != null) ? emailObject.toString() : null;

        Users existingUser = repository.findByEmail(email);
        Users savedUser;

        if (existingUser == null) {
            Users users = Users.builder()
                    .email(email)
                    .password(passwordEncoder.encode(" "))
                    .role(Role.builder().name("USER").build())
                    .build();

            savedUser = repository.save(users);
        } else {
            savedUser = existingUser;
        }

        String jwtToken = jwtService.generateToken(savedUser);

        try (PrintWriter writer = response.getWriter()) {
            writer.write(jwtToken);
        }
    }
}

