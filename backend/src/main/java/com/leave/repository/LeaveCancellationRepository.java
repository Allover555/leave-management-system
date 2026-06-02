package com.leave.repository;

import com.leave.entity.LeaveCancellation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveCancellationRepository extends JpaRepository<LeaveCancellation, Long> {

    Optional<LeaveCancellation> findByLeaveApplicationId(Long leaveId);

    @Query("SELECT lc FROM LeaveCancellation lc WHERE lc.leaveApplication.applicant.classInfo.id IN :classIds AND lc.status = 0 ORDER BY lc.createdAt DESC")
    Page<LeaveCancellation> findPendingByClassIds(@Param("classIds") List<Long> classIds, Pageable pageable);

    @Query("SELECT lc FROM LeaveCancellation lc WHERE lc.status = 0 ORDER BY lc.createdAt DESC")
    Page<LeaveCancellation> findAllPending(Pageable pageable);
}
