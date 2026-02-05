package com.rupam.saas.specification;

import com.rupam.saas.dto.TaskSearchRequest;
import com.rupam.saas.entity.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class TaskSpecification {

    public static Specification<Task> getTasks(TaskSearchRequest request, Long companyId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by Company (Mandatory)
            predicates.add(criteriaBuilder.equal(root.get("company").get("id"), companyId));

            // Filter by Title (Optional)
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                        "%" + request.getTitle().toLowerCase() + "%"));
            }

            // Filter by Status (Optional)
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
