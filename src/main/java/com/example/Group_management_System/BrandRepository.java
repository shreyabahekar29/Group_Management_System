package com.example.Group_management_System;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    // Display only active brands
    List<Brand> findByIsActiveTrue();

    // Find active brands by Chain ID
    List<Brand> findByChain_ChainIdAndIsActiveTrue(Integer chainId);

    // Find active brands by Group ID
    List<Brand> findByChain_Group_GroupIdAndIsActiveTrue(Integer groupId);

}