package com.filmland.models.payloads.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filmland.models.entities.LoggedInUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {

    @JsonIgnore
    private LoggedInUser loggedInUser;
    private ResponseMessage responseMessage;
}
