package com.filmland.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SUBSCRIBED_CATEGORY")
public class SubscribedCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="NAME")
    private String name;
    @Column(name="REMAINING_CONTENT")
    private Integer remainingContent;
    @Column(name="PRICE")
    @JsonFormat(pattern = ".#")
    private Double price;
    @Column(name="START_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "GMT+2")
    private Date startDate;

    @JsonIgnore
    @Column(name="PAYMENT_START_DATE")
    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "GMT+2")
    private Date paymentStartDate;
    @JsonIgnore
    @Column(name="CUSTOMER_ID")
    private Long customerId;

}
