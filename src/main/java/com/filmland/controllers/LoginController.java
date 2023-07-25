package com.filmland.controllers;

import com.filmland.models.entities.Customer;
import com.filmland.models.entities.LoggedInUser;
import com.filmland.models.payloads.requests.LoginRequest;
import com.filmland.models.payloads.requests.RegisterRequest;
import com.filmland.models.payloads.responses.LoginResponse;
import com.filmland.models.payloads.responses.ResponseMessage;
import com.filmland.models.repositories.CustomerRepository;
import com.filmland.models.services.CustomerService;
import com.filmland.utils.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final Set<String> sessionIds = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, RedirectAttributes redirectAttributes) {
        LoggedInUser loggedInUser = new LoggedInUser();
        try {

            if (!Helper.isValidEmailAddress(loginRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new LoginResponse(loggedInUser, new ResponseMessage("Login Failed!", "Email address entered is invalid. Please try again!")));
            }

            Optional<Customer> customer = Optional.ofNullable(customerRepository.findByEmail(loginRequest.getEmail().trim()));
            if (customer == null) {
                return ResponseEntity.badRequest().body(new LoginResponse(loggedInUser, new ResponseMessage("Login Failed!", "Email address entered is not registered. Please register!")));
            }

            String password = Helper.decodeString(customer.get().getPassword());
            if (!password.equals(loginRequest.getPassword())) {
                return ResponseEntity.badRequest().body(new LoginResponse(loggedInUser, new ResponseMessage("Login Failed!", "Incorrect password entered. Please try again!")));
            }

            loggedInUser.setEmail(loginRequest.getEmail());
            loggedInUser.setCustomerId(getUserIdFromEmail(loginRequest.getEmail()));
            loggedInUser.setSubscribedCategoryList(new ArrayList<>());

            redirectAttributes.addFlashAttribute("responseMessage", new ResponseMessage("Login Successful!", "Welcome to Filmland "+loginRequest.getEmail()+"!"));

            // Logged in user sent to back end using http headers

            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "redirect:/api/all/"+loggedInUser.getCustomerId());
            headers.add("customerId", loggedInUser.getCustomerId().toString());
            return ResponseEntity.ok().headers(headers).body(new LoginResponse(loggedInUser, new ResponseMessage("Login Successful!", "Welcome to Filmland "+loginRequest.getEmail()+"!")));
        } catch (Exception e) {
            LOGGER.error("Unable to login : " + e.getMessage());
            return ResponseEntity.badRequest().body(new LoginResponse(loggedInUser, new ResponseMessage("Login Failed!", "Incorrect email/password!")));
        }

    }

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            if (!Helper.isValidEmailAddress(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Registration Failed!", "Email address entered is invalid. Please try again!"));
            }

            if (customerRepository.findByEmail(registerRequest.getEmail()) != null) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Registration Failed!", "Email address entered is already registered. Please login!"));
            }
            if (!Helper.isValidPassword(registerRequest.getPassword())) {
                return ResponseEntity.badRequest().body(new ResponseMessage("Registration Failed!", "Password entered does not match criteria. Please try again!"));
            }
            customerService.saveCustomer(registerRequest);
            return ResponseEntity.ok(new ResponseMessage("Registration Successful!", "Welcome to Filmland "+registerRequest.getEmail()+"!"));
        } catch (Exception e) {
            LOGGER.error("Could not register new user : " + e.getMessage());
            return ResponseEntity.badRequest().body(new ResponseMessage("Registration Failed!", "Incorrect email/password!"));
        }
    }

    private Long getUserIdFromEmail(String email) {
        return customerService.findIdByEmail(email);
    }

}
