package com.leave.repository;

import com.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByTypeCode(String typeCode);

    List<LeaveType> findByStatus(Integer status);

    boolean existsByTypeCode(String typeCode);
}
