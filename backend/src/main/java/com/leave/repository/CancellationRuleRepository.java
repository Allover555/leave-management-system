package com.leave.repository;

import com.leave.entity.CancellationRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancellationRuleRepository extends JpaRepository<CancellationRule, Long> {

    Optional<CancellationRule> findFirstByStatusOrderByIdDesc(Integer status);
}
