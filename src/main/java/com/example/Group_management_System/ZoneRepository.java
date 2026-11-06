package com.example.Group_management_System;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Integer> {

    // Display only active zones
    List<Zone> findByIsActiveTrue();
    List<Zone> findByBrand_BrandIdAndIsActiveTrue(Integer brandId);
}
