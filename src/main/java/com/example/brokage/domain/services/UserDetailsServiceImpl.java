package com.example.brokage.domain.services;

import com.example.brokage.domain.exceptions.UserNotExistsException;
import com.example.brokage.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotExistsException("User not found"));

        return User.builder()
                .username(user.username())
                .password(user.password())
                .authorities(user.userType().name())
                .build();
    }
}