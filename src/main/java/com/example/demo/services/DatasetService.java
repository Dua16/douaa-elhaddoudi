package com.example.demo.services;

import com.example.demo.entities.Dataset;
import com.example.demo.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasetService {
    @Autowired
    private DatasetRepository datasetRepository;
    public List<Dataset> getAll(){
        return datasetRepository.findAll();
    }
}
