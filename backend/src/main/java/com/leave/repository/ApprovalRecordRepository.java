package com.leave.repository;

import com.leave.entity.ApprovalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRecordRepository extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByLeaveApplicationIdOrderByApprovedAtAsc(Long leaveId);

    @Query("SELECT ar FROM ApprovalRecord ar WHERE ar.approver.id = :approverId ORDER BY ar.approvedAt DESC")
    Page<ApprovalRecord> findByApproverId(@Param("approverId") Long approverId, Pageable pageable);
}
