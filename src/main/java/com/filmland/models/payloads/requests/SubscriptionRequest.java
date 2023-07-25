package com.filmland.models.payloads.requests;

import lombok.Data;

@Data
public class SubscriptionRequest {

    private String email;

    private String availableCategory;
}
