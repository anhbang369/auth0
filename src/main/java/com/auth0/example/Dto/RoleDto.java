package com.auth0.example.Dto;

import lombok.*;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleDto {
    private Integer id;

    private String name;
}
