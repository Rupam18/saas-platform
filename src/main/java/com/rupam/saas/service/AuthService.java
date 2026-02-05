package com.rupam.saas.service;

import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.entity.Company;
import com.rupam.saas.entity.Role;
import com.rupam.saas.entity.User;
import com.rupam.saas.repository.RoleRepository;
import com.rupam.saas.repository.UserRepository;
import com.rupam.saas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final CompanyService companyService; // Use service instead of repo if possible, or repo
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create company
        Company company = companyService.createCompany(req.getCompanyName(), req.getEmail());

        // Get role
        String roleName = req.getRole().toUpperCase();
        Role role = roleRepo.findByName(roleName)
                .orElseGet(() -> roleRepo.save(new Role(null, roleName)));

        // Create user
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setCompany(company);
        user.setRoles(Set.of(role));

        userRepo.save(user);

        return "Company and Admin created successfully";
    }

    public String login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRoles().iterator().next().getName(),
                user.getCompany().getId());
    }
}
