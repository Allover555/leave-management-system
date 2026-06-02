package com.leave.repository;

import com.leave.entity.CounselorClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounselorClassRepository extends JpaRepository<CounselorClass, Long> {

    @Query("SELECT cc.classInfo.id FROM CounselorClass cc WHERE cc.counselor.id = :counselorId")
    List<Long> findClassIdsByCounselorId(@Param("counselorId") Long counselorId);

    List<CounselorClass> findByCounselorId(Long counselorId);

    void deleteByCounselorIdAndClassInfoId(Long counselorId, Long classId);
}
