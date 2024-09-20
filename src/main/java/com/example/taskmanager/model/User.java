package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Allows easy construction of User objects
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Implementing UserDetails methods

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // Assuming role.getName() returns "ROLE_USER" or "ROLE_ADMIN"
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Can modify this to check for expiration logic if necessary
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Add custom logic if you need account locking
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Modify to check for credential expiration
    }

    @Override
    public boolean isEnabled() {
        return true; // Use this to activate or deactivate accounts
    }
}

