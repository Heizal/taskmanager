package com.example.taskmanager.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class BaseService {
    public abstract void findById(Long id);
    public abstract List<?> getAll();
    public abstract void delete(Long id);
}
