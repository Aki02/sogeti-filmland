package com.filmland.models.payloads.requests;

import lombok.Data;

@Data
public class AvailableCategoryRequest {

    private String name;

    private int availableCategory;

    private double price;
}
