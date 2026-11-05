
package com.example.Group_management_System;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {

    List<Brand> findByIsActiveTrue();

    List<Brand> findByChain_ChainIdAndIsActiveTrue(Integer chainId);

    List<Brand> findByChain_Group_GroupIdAndIsActiveTrue(Integer groupId);
}
