package com.filmland.models.payloads.responses;

import com.filmland.models.entities.AvailableCategory;
import com.filmland.models.entities.SubscribedCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CategoryResponse {

    private List<AvailableCategory> availableCategories;
    private List<SubscribedCategory> subscribedCategories;

}
