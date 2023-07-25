package com.filmland.models.services;

import com.filmland.models.entities.AvailableCategory;
import com.filmland.models.entities.SubscribedCategory;
import com.filmland.models.payloads.responses.CategoryResponse;

public interface CategoryService {

    CategoryResponse findAll();
    CategoryResponse findAllByCustomerId(Long id);

    void saveSubscription(Long id, AvailableCategory availableCategory);
    void saveSubscription(Long id, AvailableCategory availableCategory, int remainingContent, double fee);
    void updateSubscription(SubscribedCategory subscribedCategory, int remainingContent, double fee);

    void updateRemainingContent();

}
