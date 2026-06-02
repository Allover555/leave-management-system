package com.leave.repository;

import com.leave.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Long> {

    List<ClassInfo> findByDepartmentId(Long departmentId);

    List<ClassInfo> findByStatus(Integer status);
}
