package com.rupam.saas.controller;

import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.entity.Role;
import com.rupam.saas.entity.User;
import com.rupam.saas.repository.RoleRepository;
import com.rupam.saas.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public AdminController(UserRepository userRepo,
                           RoleRepository roleRepo,
                           PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-user")
    public String createUser(@RequestBody RegisterRequest req, HttpServletRequest request) {

        Long companyId = (Long) request.getAttribute("companyId");

        Role role = roleRepo.findByName("USER")
                .orElseGet(() -> roleRepo.save(new Role(null, "USER")));

        User admin = userRepo.findByEmail(request.getUserPrincipal().getName()).get();

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRoles(Set.of(role));
        user.setCompany(admin.getCompany());

        userRepo.save(user);

        return "User created inside your company";
    }
}

