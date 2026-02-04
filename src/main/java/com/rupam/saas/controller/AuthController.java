package com.rupam.saas.controller;

import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.entity.Company;
import com.rupam.saas.entity.Role;
import com.rupam.saas.entity.User;
import com.rupam.saas.repository.CompanyRepository;
import com.rupam.saas.repository.RoleRepository;
import com.rupam.saas.repository.UserRepository;
import com.rupam.saas.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/")
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final CompanyRepository companyRepo;


    public AuthController(
        UserRepository userRepo,
        RoleRepository roleRepo,
        CompanyRepository companyRepo,
        PasswordEncoder encoder,
        JwtUtil jwtUtil
) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
    this.companyRepo = companyRepo;   // <-- ADD THIS
    this.encoder = encoder;
    this.jwtUtil = jwtUtil;
}


    // ================= REGISTER =================
    @PostMapping("/register")
public String register(@RequestBody RegisterRequest req) {

    if (userRepo.findByEmail(req.getEmail()).isPresent()) {
        throw new RuntimeException("Email already registered");
    }

    // Create company
    Company company = new Company();
    company.setName(req.getCompanyName());
    company.setEmail(req.getEmail());
    companyRepo.save(company);

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

    // ================= LOGIN =================
    @PostMapping("/login")
public String login(@RequestBody LoginRequest req) {

    User user = userRepo.findByEmail(req.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!encoder.matches(req.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid password");
    }

    String token = jwtUtil.generateToken(
    user.getEmail(),
    user.getRoles().iterator().next().getName(),
    user.getCompany().getId()
);


    return token;
}

}
