package com.example.Group_management_System;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimateRepository
        extends JpaRepository<Estimate, Integer> {

}
