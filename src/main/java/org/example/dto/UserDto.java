package org.example.dto;

public record UserDto(
        Long id,
        String username,
        String email
) {}
