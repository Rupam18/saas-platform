package com.rupam.saas.service;

import com.rupam.saas.entity.Company;
import com.rupam.saas.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepo;

    public Company createCompany(String name, String email) {
        if (companyRepo.findByName(name).isPresent()) {
            throw new RuntimeException("Company with this name already exists");
        }
        Company company = new Company();
        company.setName(name);
        company.setEmail(email);
        return companyRepo.save(company);
    }

    public Company getCompanyById(Long id) {
        return companyRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }
}
