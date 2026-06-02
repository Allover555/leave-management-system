package com.leave.repository;

import com.leave.entity.ApprovalFlowConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalFlowConfigRepository extends JpaRepository<ApprovalFlowConfig, Long> {

    @Query("SELECT f FROM ApprovalFlowConfig f WHERE f.status = 1 AND (f.leaveType.id = :leaveTypeId OR f.leaveType IS NULL) AND :days BETWEEN f.minDays AND f.maxDays ORDER BY f.leaveType.id DESC NULLS LAST")
    List<ApprovalFlowConfig> findMatchingFlow(@Param("leaveTypeId") Long leaveTypeId, @Param("days") Integer days);

    List<ApprovalFlowConfig> findByStatus(Integer status);
}
