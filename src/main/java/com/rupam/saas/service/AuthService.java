package com.rupam.saas.service;

import com.rupam.saas.dto.LoginRequest;
import com.rupam.saas.dto.RegisterRequest;
import com.rupam.saas.entity.Company;
import com.rupam.saas.entity.Role;
import com.rupam.saas.entity.User;
import com.rupam.saas.repository.RoleRepository;
import com.rupam.saas.repository.UserRepository;
import com.rupam.saas.repository.InvitationRepository;
import com.rupam.saas.repository.CompanyRepository;
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
    private final CompanyService companyService;
    private final InvitationRepository invitationRepo; // New dependency
    private final CompanyRepository companyRepo; // New dependency
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public String register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Company company;
        String roleName;

        // Check if registering via invitation
        if (req.getInvitationToken() != null && !req.getInvitationToken().isEmpty()) {
            com.rupam.saas.entity.Invitation invitation = invitationRepo.findByToken(req.getInvitationToken())
                    .orElseThrow(() -> new RuntimeException("Invalid invitation token"));

            if (invitation.isAccepted()) {
                throw new RuntimeException("Invitation already accepted");
            }

            if (!invitation.getEmail().equalsIgnoreCase(req.getEmail())) {
                throw new RuntimeException("Email does not match invitation");
            }

            company = companyRepo.findById(invitation.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            roleName = invitation.getRole();

            // Mark as accepted
            invitation.setAccepted(true);
            invitationRepo.save(invitation);

            // Skip company name check for invited users
        } else {
            // Standard registration: Create new company
            company = companyService.createCompany(req.getCompanyName(), req.getEmail());
            roleName = req.getRole().toUpperCase();
        }

        // Get or create role
        String finalRoleName = roleName;
        Role role = roleRepo.findByName(finalRoleName)
                .orElseGet(() -> roleRepo.save(new Role(null, finalRoleName)));

        // Create user
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setCompany(company);
        user.setRoles(Set.of(role));

        try {
            userRepo.save(user);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("Email already exists");
        }

        return "User registered successfully";
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
