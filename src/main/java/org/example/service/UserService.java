package org.example.service;

import org.example.email.exceptions.UserNotFoundException;
import org.example.entity.MyUser;
import org.example.repository.MyUserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    MyUserRepository userRepository;

    public UserService(MyUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "users", key = "#username")
    public MyUser loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user;
    }

    public MyUser loadUserByEmail(String email) throws UserNotFoundException {
        MyUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return user;
    }
}
