package com.filmland.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CUSTOMER")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="EMAIL")
    private String email;
    @Column(name="PASSWORD")
    private String password;
    @Column(name="REGISTRATION_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "GMT+2")
    private Date registrationDate;
    @Column(name="ACTIVE_SUBSCRIPTION")
    private Boolean activeSubscription;
    @Column(name="SUBSCRIPTION_FEE")
    @JsonFormat(pattern = ".#")
    private Double subscriptionFee;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,targetEntity = SubscribedCategory.class)
    @JoinColumn(name="CUSTOMER_ID",referencedColumnName = "id")
    private Collection<SubscribedCategory> subscribedCategoryCollection;

}
