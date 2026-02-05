package com.rupam.saas.service;

import com.rupam.saas.dto.UserResponse;
import com.rupam.saas.entity.User;
import com.rupam.saas.exception.ResourceNotFoundException;
import com.rupam.saas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public UserResponse getUserProfile(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    // Admin only - potentially
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getAllUsersByCompany(String currentUserEmail) {
        User currentUser = userRepo.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (currentUser.getCompany() == null) {
            return List.of(mapToResponse(currentUser));
        }

        return userRepo.findByCompanyId(currentUser.getCompany().getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCompany() != null ? user.getCompany().getName() : null,
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
    }
}
