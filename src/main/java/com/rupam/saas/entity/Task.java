package com.rupam.saas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    // TODO, IN_PROGRESS, DONE
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    public Task() {
    }

    // ---------- Getters ----------
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public Company getCompany() {
        return company;
    }

    // ---------- Setters ----------
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
