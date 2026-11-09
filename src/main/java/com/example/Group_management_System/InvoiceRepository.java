
package com.example.Group_management_System;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    boolean existsByInvoiceNo(Integer invoiceNo);

}

