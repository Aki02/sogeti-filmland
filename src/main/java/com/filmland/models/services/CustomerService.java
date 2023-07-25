package com.filmland.models.services;

import com.filmland.models.entities.Customer;
import com.filmland.models.payloads.requests.RegisterRequest;

public interface CustomerService {

    void saveCustomer(RegisterRequest registerRequest);

    void updateCustomer(Customer customer, double fee);

    Long findIdByEmail(String email);

}
