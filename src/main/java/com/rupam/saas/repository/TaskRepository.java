package com.rupam.saas.repository;

import com.rupam.saas.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    // All tasks of a company
    List<Task> findByCompanyId(Long companyId);

    // Count all tasks of a company
    long countByCompanyId(Long companyId);

    // Count tasks by status for a company
    long countByCompanyIdAndStatus(Long companyId, String status);
}
