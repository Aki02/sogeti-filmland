package com.filmland.models.services;

import com.filmland.models.entities.Customer;
import com.filmland.models.payloads.requests.RegisterRequest;
import com.filmland.models.repositories.CustomerRepository;
import com.filmland.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void saveCustomer(RegisterRequest registerRequest) {
        Customer customer = new Customer();
        customer.setEmail(registerRequest.getEmail().trim());
        customer.setPassword(Helper.encodeString(registerRequest.getPassword().trim()));
        customer.setActiveSubscription(true);
        customer.setSubscriptionFee(0.0);
        customer.setRegistrationDate(new Date());
        customerRepository.save(customer);
    }

    @Override
    public void updateCustomer(Customer customer, double fee) {
        customer.setSubscriptionFee(fee);
        customerRepository.save(customer);
    }

    @Override
    public Long findIdByEmail(String email) {
        return customerRepository.findByEmail(email).getId();
    }

}
