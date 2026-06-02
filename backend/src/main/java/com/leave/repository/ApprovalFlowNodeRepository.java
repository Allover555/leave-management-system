package com.leave.repository;

import com.leave.entity.ApprovalFlowNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalFlowNodeRepository extends JpaRepository<ApprovalFlowNode, Long> {

    List<ApprovalFlowNode> findByFlowIdOrderByNodeOrderAsc(Long flowId);
}
