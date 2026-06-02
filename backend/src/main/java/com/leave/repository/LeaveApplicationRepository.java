package com.leave.repository;

import com.leave.entity.LeaveApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    Optional<LeaveApplication> findByLeaveNo(String leaveNo);

    @Query("SELECT la FROM LeaveApplication la WHERE la.applicant.id = :userId ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findByApplicantId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la WHERE la.applicant.id = :userId AND la.status = :status ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findByApplicantIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status, Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la JOIN ApprovalFlowNode n ON la.currentNodeId = n.id WHERE la.applicant.classInfo.id IN :classIds AND la.status IN (0, 1) AND n.approverRole.roleCode = 'COUNSELOR' ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findPendingByClassIds(@Param("classIds") List<Long> classIds, Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la WHERE la.applicant.classInfo.id IN :classIds ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findByClassIds(@Param("classIds") List<Long> classIds, Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la WHERE la.status = :status ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findByStatus(@Param("status") Integer status, Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findAllOrdered(Pageable pageable);

    @Query("SELECT la FROM LeaveApplication la JOIN ApprovalFlowNode n ON la.currentNodeId = n.id WHERE la.status IN (0, 1) AND n.approverRole.roleCode = :roleCode ORDER BY la.createdAt DESC")
    Page<LeaveApplication> findPendingByApproverRole(@Param("roleCode") String roleCode, Pageable pageable);

    @Query("SELECT COUNT(la) FROM LeaveApplication la WHERE la.applicant.id = :userId AND la.createdAt BETWEEN :start AND :end")
    Long countByApplicantAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT la FROM LeaveApplication la WHERE la.status = 2 AND la.endTime < :now AND la.id NOT IN (SELECT lc.leaveApplication.id FROM LeaveCancellation lc)")
    List<LeaveApplication> findOverdueUncancelled(@Param("now") LocalDateTime now);

    @Query("SELECT la.leaveType.typeName, COUNT(la) FROM LeaveApplication la WHERE la.createdAt BETWEEN :start AND :end GROUP BY la.leaveType.typeName")
    List<Object[]> countByTypeAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT la.applicant.classInfo.className, COUNT(la) FROM LeaveApplication la WHERE la.createdAt BETWEEN :start AND :end GROUP BY la.applicant.classInfo.className")
    List<Object[]> countByClassAndDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(la.duration), 0) FROM LeaveApplication la WHERE la.applicant.id = :userId AND la.status IN (2, 5) AND la.createdAt BETWEEN :start AND :end")
    Double sumDurationByUserAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
