package com.rupam.saas.controller;

import com.rupam.saas.dto.InvitationRequest;
import com.rupam.saas.entity.Invitation;
import com.rupam.saas.service.InvitationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    private Long getCompanyId(HttpServletRequest request) {
        return (Long) request.getAttribute("companyId");
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public Invitation sendInvite(@Valid @RequestBody InvitationRequest req, HttpServletRequest request) {
        // In a real app, verify that the requester is an ADMIN here
        return invitationService.sendInvitation(req, getCompanyId(request));
    }

    @GetMapping("/{token}")
    public Invitation getInvite(@PathVariable String token) {
        return invitationService.getInvitation(token);
    }
}
