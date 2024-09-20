package com.example.taskmanager.service;


import com.example.taskmanager.model.Role;
import com.example.taskmanager.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Role createRole(String roleName) {
        if (roleRepository.findByName(roleName) != null) {
            throw new RuntimeException("Role already exists");
        }
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }
}
