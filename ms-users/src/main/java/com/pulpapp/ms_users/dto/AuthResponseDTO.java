package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String email;
    private String name;
    private Role role;
}
