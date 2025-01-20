package com.excelr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.model.Cart;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {

    // Find all cart items for a specific user
    List<Cart> findByUserId(Long userId);

    // Optionally, add a method to delete all cart items for a user
    void deleteByUserId(Long userId);
}
