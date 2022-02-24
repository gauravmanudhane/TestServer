package com.ethoca.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ethoca.customer.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
