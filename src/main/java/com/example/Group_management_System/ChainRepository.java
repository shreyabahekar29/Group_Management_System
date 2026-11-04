
package com.example.Group_management_System;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChainRepository extends JpaRepository<Chain, Integer> {

    boolean existsByGstnNo(String gstnNo);

    List<Chain> findByGroup_GroupIdAndIsActiveTrue(Integer groupId);

    List<Chain> findByIsActiveTrue();
    boolean existsByGstnNoAndChainIdNot(String gstnNo, Integer chainId);
}