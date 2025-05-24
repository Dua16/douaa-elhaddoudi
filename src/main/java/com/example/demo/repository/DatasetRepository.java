package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.Dataset;

public interface DatasetRepository extends JpaRepository<Dataset,Long> {
}
