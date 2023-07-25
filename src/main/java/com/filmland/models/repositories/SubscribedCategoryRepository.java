package com.filmland.models.repositories;

import com.filmland.models.entities.SubscribedCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribedCategoryRepository extends JpaRepository<SubscribedCategory, Long> {

    SubscribedCategory findByName(String name);
    SubscribedCategory findByNameAndCustomerId(String name, Long Id);
    List<SubscribedCategory> findAllByCustomerId(Long id);
}
