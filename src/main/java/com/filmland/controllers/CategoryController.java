package com.filmland.controllers;

import com.filmland.models.entities.AvailableCategory;
import com.filmland.models.entities.Customer;
import com.filmland.models.entities.SubscribedCategory;
import com.filmland.models.payloads.requests.ShareSubscriptionRequest;
import com.filmland.models.payloads.requests.SubscriptionRequest;
import com.filmland.models.payloads.responses.CategoryResponse;
import com.filmland.models.payloads.responses.ResponseMessage;
import com.filmland.models.repositories.AvailableCategoryRepository;
import com.filmland.models.repositories.CustomerRepository;
import com.filmland.models.repositories.SubscribedCategoryRepository;
import com.filmland.models.services.CategoryService;
import com.filmland.models.services.CustomerService;
import com.filmland.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private AvailableCategoryRepository availableCategoryRepository;
    @Autowired
    private SubscribedCategoryRepository subscribedCategoryRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CustomerService customerService;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    // Used for testing purposes
    @GetMapping("/all")
    ResponseEntity<CategoryResponse> findAll() {
        try {
            return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Could not find all categories with no filtering on use : " + e.getMessage());
            return new ResponseEntity<>(new CategoryResponse(new ArrayList<>(), new ArrayList<>()), HttpStatus.OK);
        }
    }

    @GetMapping("/all/{id}")
    ResponseEntity<CategoryResponse> findById(@PathVariable Long id){
        try {

            System.out.println("customerId = "+id);
            Optional<Customer> customer = customerRepository.findById(id);
            if (customer == null || !customer.isPresent()) {
                LOGGER.error("Customer needs to be registered and logged in to view this.");
                return new ResponseEntity<>(new CategoryResponse(new ArrayList<>(), new ArrayList<>()), HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(categoryService.findAllByCustomerId(id), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Could not find categories from user logged in : "+e.getMessage());
        }
        return new ResponseEntity<>(new CategoryResponse(new ArrayList<>(), new ArrayList<>()), HttpStatus.OK);
    }

    @PostMapping("/subscribe")
    ResponseEntity subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {

        try {
            if (!Helper.isValidEmailAddress(subscriptionRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Login Failed!", "Email address entered is invalid. Please try again!"));
            }

            Optional<AvailableCategory> availableCategory = Optional.ofNullable(availableCategoryRepository.findByName(subscriptionRequest.getAvailableCategory()));
            if (availableCategory == null) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Failed!", "Invalid category selected for subscription!"));
            }

            if (subscribedCategoryRepository.findByNameAndCustomerId(subscriptionRequest.getAvailableCategory(), getCustomerIdFromEmail(subscriptionRequest.getEmail())) != null) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Failed!", "User already subscribed!"));
            }

            long customerId = getCustomerIdFromEmail(subscriptionRequest.getEmail());
            double fee = availableCategory.get().getPrice() + calculateSubscriptionFee(subscribedCategoryRepository.findAllByCustomerId(customerId));
            updateCustomerSubscription(customerId, fee);
            categoryService.saveSubscription(getCustomerIdFromEmail(subscriptionRequest.getEmail()), availableCategory.get());

            return ResponseEntity.ok(new ResponseMessage("Subscription to " + subscriptionRequest.getAvailableCategory() + " Successful!",
                    "Subscription Start date: " + sdf.format(new Date()) + "! Payment Start Date: "+ sdf.format(Helper.calculatePaymentDate(new Date())) +". " +
                            "Subscription Fee = " + availableCategory.get().getPrice() + ". " +
                            "New Total Subscription Fee = " + fee));
        } catch (Exception e) {
            LOGGER.error("Could not find subscribe to new category for user logged in : " + e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "Unexpected error received. Please try again later!"));
        }
    }

    @PostMapping("/share")
    ResponseEntity shareSubscription(@RequestBody ShareSubscriptionRequest shareSubscriptionRequest) {

        try {

            if (!Helper.isValidEmailAddress(shareSubscriptionRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "User Email address entered is invalid. Please try again!"));
            }

            if (!Helper.isValidEmailAddress(shareSubscriptionRequest.getCustomer())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "Customer Email address entered is invalid. Please try again!"));
            }

            Optional<SubscribedCategory> subscribedCategory = Optional.ofNullable(subscribedCategoryRepository.findByName(shareSubscriptionRequest.getSubscribedCategory()));
            if (subscribedCategory == null) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "Invalid current subscription category chosen! Please try again!"));
            }

            if (subscribedCategoryRepository.findByNameAndCustomerId(shareSubscriptionRequest.getSubscribedCategory(), getCustomerIdFromEmail(shareSubscriptionRequest.getCustomer())) != null) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "Customer already subscribed to category chosen for sharing!"));
            }

            if (subscribedCategoryRepository.findByNameAndCustomerId(shareSubscriptionRequest.getSubscribedCategory(), getCustomerIdFromEmail(shareSubscriptionRequest.getEmail())) == null) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "User not subscribed to category chosen!"));
            }

            double fee = subscribedCategory.get().getPrice()/2;
            LOGGER.debug("Fee cost after sharing = "+fee);

            int remainingContent = 0;

            if (subscribedCategory.get().getRemainingContent()/2 > 1) {
                 remainingContent = subscribedCategory.get().getRemainingContent() / 2;
            }

            Optional<AvailableCategory> availableCategory = Optional.ofNullable(availableCategoryRepository.findByName(shareSubscriptionRequest.getSubscribedCategory()));
            categoryService.saveSubscription(getCustomerIdFromEmail(shareSubscriptionRequest.getCustomer()), availableCategory.get(), remainingContent, fee);

            Long userId = getCustomerIdFromEmail(shareSubscriptionRequest.getEmail());
            Long customerId = getCustomerIdFromEmail(shareSubscriptionRequest.getCustomer());
            double userFee = calculateSubscriptionFee(subscribedCategoryRepository.findAllByCustomerId(userId)) - fee;
            double customerFee = calculateSubscriptionFee(subscribedCategoryRepository.findAllByCustomerId(customerId));

            updateSubscription(subscribedCategory.get(), remainingContent, fee);
            updateCustomerSubscription(userId, userFee);
            updateCustomerSubscription(customerId, customerFee);

            return ResponseEntity.ok(new ResponseMessage("Subscription Sharing of "+shareSubscriptionRequest.getSubscribedCategory()+" was Successful!",
                    "User "+shareSubscriptionRequest.getEmail()+" New Total Subscription Fee = "+fee+" and Updated Remaining Content = "+remainingContent));
        } catch (Exception e) {
            LOGGER.error("Could not share subscription : " + e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseMessage("Subscription Sharing Failed!", "Unexpected error received. Please try again later!"));
        }
    }

    private double calculateSubscriptionFee(List<SubscribedCategory> subscribedCategoryList) {
        double fee = 0;
        for (SubscribedCategory subscribedCategory : subscribedCategoryList) {
            fee += subscribedCategory.getPrice();
        }
        return fee;
    }

    private void updateCustomerSubscription(Long id, double fee) {
        Optional<Customer> customer = customerRepository.findById(id);
        customerService.updateCustomer(customer.get(), fee);
    }

    private void updateSubscription(SubscribedCategory subscribedCategory, int remainingContent, double fee) {
        categoryService.updateSubscription(subscribedCategory, remainingContent, fee);
    }

    private Long getCustomerIdFromEmail(String email) {
        System.out.println("Email returned = "+customerService.findIdByEmail(email));
        return customerService.findIdByEmail(email);
    }

}