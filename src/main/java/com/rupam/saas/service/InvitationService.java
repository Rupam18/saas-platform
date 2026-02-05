package com.rupam.saas.service;

import com.rupam.saas.dto.InvitationRequest;
import com.rupam.saas.entity.Invitation;
import com.rupam.saas.repository.InvitationRepository;
import com.rupam.saas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepo;
    private final UserRepository userRepo;

    public Invitation sendInvitation(InvitationRequest req, Long companyId) {
        // Check if user already exists
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists.");
        }

        // Check if invitation already exists
        if (invitationRepo.findByEmailAndCompanyId(req.getEmail(), companyId).isPresent()) {
            throw new RuntimeException("Invitation already sent to this email.");
        }

        String token = UUID.randomUUID().toString();
        Invitation invitation = new Invitation(req.getEmail(), token, companyId,
                req.getRole() != null ? req.getRole() : "USER");

        Invitation saved = invitationRepo.save(invitation);

        // MOCK EMAIL SENDING
        System.out.println("==================================================");
        System.out.println(" SENDING INVITE TO: " + req.getEmail());
        System.out.println(" JOIN LINK: http://localhost:5173/accept-invite?token=" + token);
        System.out.println("==================================================");

        return saved;
    }

    public Invitation getInvitation(String token) {
        return invitationRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired invitation token"));
    }
}
