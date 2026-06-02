package com.leave.repository;

import com.leave.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM SysUser u WHERE u.role.roleCode = :roleCode")
    List<SysUser> findByRoleCode(@Param("roleCode") String roleCode);

    @Query("SELECT u FROM SysUser u WHERE u.classInfo.id = :classId AND u.role.roleCode = 'STUDENT'")
    List<SysUser> findStudentsByClassId(@Param("classId") Long classId);

    @Query("SELECT u FROM SysUser u WHERE u.department.id = :deptId")
    List<SysUser> findByDepartmentId(@Param("deptId") Long deptId);

    @Query("SELECT u FROM SysUser u WHERE (:keyword IS NULL OR u.realName LIKE %:keyword% OR u.username LIKE %:keyword%) AND (:roleCode IS NULL OR u.role.roleCode = :roleCode)")
    Page<SysUser> searchUsers(@Param("keyword") String keyword, @Param("roleCode") String roleCode, Pageable pageable);
}
