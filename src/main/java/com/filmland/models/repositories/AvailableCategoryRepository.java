package com.filmland.models.repositories;

import com.filmland.models.entities.AvailableCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableCategoryRepository extends JpaRepository<AvailableCategory, Long> {

    AvailableCategory findByName(String name);
}
