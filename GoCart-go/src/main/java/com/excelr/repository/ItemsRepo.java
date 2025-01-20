package com.excelr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.excelr.model.Items;

@Repository
public interface ItemsRepo extends JpaRepository<Items, Long> {
   List<Items> findByCategory(String category);
}
