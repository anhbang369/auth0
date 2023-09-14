package com.auth0.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/branches")
@EnableWebSecurity
public class TestController {

    @GetMapping("/hihi")
    @PreAuthorize("hasRole('ROLE_VN')")
    public String getByBranch() {
        return "hello user";
    }
}
