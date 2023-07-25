package com.filmland.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AVAILABLE_CATEGORY")
public class AvailableCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="NAME")
    private String name;
    @Column(name="AVAILABLE_CONTENT")
    private Integer availableContent;
    @Column(name="PRICE")
    @JsonFormat(pattern = ".#")
    private Double price;

}