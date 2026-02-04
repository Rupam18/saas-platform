package com.rupam.saas.controller;

import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.entity.Role;
import com.rupam.saas.entity.User;
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

    public AuthController(
            UserRepository userRepo,
            RoleRepository roleRepo,
            PasswordEncoder encoder,
            JwtUtil jwtUtil) {

        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {

        // Check if email already exists
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Get or create ADMIN role
        Role adminRole = roleRepo.findByName("ADMIN")
                .orElseGet(() -> roleRepo.save(new Role(null, "ADMIN")));

        // Create user
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRoles(Set.of(adminRole));

        userRepo.save(user);

        return "User registered successfully";
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
            user.getRoles().iterator().next().getName()
    );

    return token;
}

}
