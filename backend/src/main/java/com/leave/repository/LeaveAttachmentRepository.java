package com.leave.repository;

import com.leave.entity.LeaveAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveAttachmentRepository extends JpaRepository<LeaveAttachment, Long> {

    List<LeaveAttachment> findByLeaveApplicationId(Long leaveId);
}
