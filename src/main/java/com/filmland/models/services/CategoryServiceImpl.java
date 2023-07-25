package com.filmland.models.services;

import com.filmland.models.entities.AvailableCategory;
import com.filmland.models.entities.SubscribedCategory;
import com.filmland.models.payloads.responses.CategoryResponse;
import com.filmland.models.repositories.AvailableCategoryRepository;
import com.filmland.models.repositories.CustomerRepository;
import com.filmland.models.repositories.SubscribedCategoryRepository;
import com.filmland.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.filmland.utils.Helper.isDatePassed;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private AvailableCategoryRepository availableCategoryRepository;
    @Autowired
    private SubscribedCategoryRepository subscribedCategoryRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CategoryResponse findAll() {
        List<AvailableCategory> availableCategories = new ArrayList<>();
        List<SubscribedCategory> subscribedCategories = new ArrayList<>();
        availableCategoryRepository.findAll().forEach(availableCategories::add);
        subscribedCategoryRepository.findAll().forEach(subscribedCategories::add);

        return new CategoryResponse(availableCategories, subscribedCategories);
    }

    @Override
    public CategoryResponse findAllByCustomerId(Long id) {
        List<AvailableCategory> availableCategories = new ArrayList<>();
        List<SubscribedCategory> subscribedCategories = new ArrayList<>();
        availableCategoryRepository.findAll().forEach(availableCategories::add);
        subscribedCategoryRepository.findAllByCustomerId(id).forEach(subscribedCategories::add);
        List<AvailableCategory> removeSubscribedList = new ArrayList<>();

        for (SubscribedCategory subscribedCategory : subscribedCategories) {
            for (AvailableCategory availableCategory : availableCategories) {
                if (availableCategory.getName().equals(subscribedCategory.getName())) {
                    removeSubscribedList.add(availableCategory);
                }
            }
        }
        availableCategories.removeAll(removeSubscribedList);
        return new CategoryResponse(availableCategories, subscribedCategories);
    }

    @Override
    public void saveSubscription(Long id, AvailableCategory availableCategory) {
        SubscribedCategory subscribedCategory = new SubscribedCategory();
        subscribedCategory.setName(availableCategory.getName());
        subscribedCategory.setRemainingContent(availableCategory.getAvailableContent());
        subscribedCategory.setPrice(availableCategory.getPrice());
        subscribedCategory.setStartDate(new Date());
        subscribedCategory.setCustomerId(id);
        subscribedCategory.setPaymentStartDate(Helper.calculatePaymentDate(new Date()));
        subscribedCategoryRepository.save(subscribedCategory);
    }

    @Override
    public void saveSubscription(Long id, AvailableCategory availableCategory, int remainingContent, double fee) {
        SubscribedCategory subscribedCategory = new SubscribedCategory();
        subscribedCategory.setName(availableCategory.getName());
        subscribedCategory.setRemainingContent(remainingContent);
        subscribedCategory.setPrice(fee);
        subscribedCategory.setStartDate(new Date());
        subscribedCategory.setCustomerId(id);
        subscribedCategory.setPaymentStartDate(Helper.calculatePaymentDate(new Date()));
        subscribedCategoryRepository.save(subscribedCategory);
    }

    @Override
    public void updateSubscription(SubscribedCategory subscribedCategory, int remainingContent, double fee) {
        subscribedCategory.setRemainingContent(remainingContent);
        subscribedCategory.setPrice(fee);
        subscribedCategoryRepository.save(subscribedCategory);
    }

    @Override
    public void updateRemainingContent() {
        List<SubscribedCategory> subscribedCategories = new ArrayList<>();
        List<SubscribedCategory> updatedSubscribedCategories = new ArrayList<>();
        subscribedCategoryRepository.findAll().forEach(subscribedCategories::add);

        for (SubscribedCategory subscribedCategory : subscribedCategories) {
            if (isDatePassed(subscribedCategory.getStartDate())) {
                subscribedCategory.setRemainingContent(0);
                updatedSubscribedCategories.add(subscribedCategory);
            }
        }
        subscribedCategoryRepository.saveAll(updatedSubscribedCategories);
    }

}
