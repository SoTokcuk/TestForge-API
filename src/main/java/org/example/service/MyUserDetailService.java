package org.example.service;

import org.example.email.exceptions.UserNotFoundException;
import org.example.entity.MyUser;
import org.example.repository.MyUserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    private MyUserRepository myUserRepository;
    public MyUserDetailService(MyUserRepository userRepository) {
        this.myUserRepository = userRepository;
    }

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser user = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(getRoles(user))
                .build();
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        MyUser user = myUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(getRoles(user))
                .build();
    }

    private String[] getRoles(MyUser user) {
        if (user.getRole() == null) {
            return new String[]{"USER"};
        }
        return user.getRole().split(",");
    }

    public MyUser registerUser(String username, String email, String password) {
        if (myUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (myUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        String encodedPassword  = passwordEncoder.encode(password);
        MyUser user = new MyUser(username, email, encodedPassword);
        user.setRole("USER");
        return myUserRepository.save(user);
    }
}