package com.filmland.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoggedInUser {

    private String email;
    private Long customerId;
    private List<SubscribedCategory> subscribedCategoryList;
}
