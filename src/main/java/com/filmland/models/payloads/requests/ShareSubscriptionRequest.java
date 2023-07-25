package com.filmland.models.payloads.requests;

import lombok.Data;

@Data
public class ShareSubscriptionRequest {

    private String email;

    private String customer;

    private String subscribedCategory;
}
