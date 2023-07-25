package com.filmland.models.payloads.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String email;

    private String password;

    // Option to expand and maybe add a username field at a later stage
}
