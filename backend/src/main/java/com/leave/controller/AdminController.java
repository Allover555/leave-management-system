package com.leave.controller;

import com.leave.common.Result;
import com.leave.entity.*;
import com.leave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final SysRoleRepository roleRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ApprovalFlowConfigRepository flowConfigRepository;
    private final ApprovalFlowNodeRepository flowNodeRepository;
    private final DepartmentRepository departmentRepository;
    private final ClassInfoRepository classInfoRepository;
    private final CounselorClassRepository counselorClassRepository;
    private final CancellationRuleRepository cancellationRuleRepository;
    private final SysUserRepository userRepository;

    // ===== 角色管理 =====
    @GetMapping("/roles")
    public Result<List<SysRole>> getAllRoles() {
        return Result.success(roleRepository.findAll());
    }

    @PostMapping("/roles")
    public Result<SysRole> createRole(@RequestBody SysRole role) {
        return Result.success(roleRepository.save(role));
    }

    @PutMapping("/roles/{id}")
    public Result<SysRole> updateRole(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        return Result.success(roleRepository.save(role));
    }

    // ===== 假别管理 =====
    @GetMapping("/leave-types")
    public Result<List<LeaveType>> getLeaveTypes() {
        return Result.success(leaveTypeRepository.findAll());
    }

    @PostMapping("/leave-types")
    public Result<LeaveType> createLeaveType(@RequestBody LeaveType leaveType) {
        return Result.success(leaveTypeRepository.save(leaveType));
    }

    @PutMapping("/leave-types/{id}")
    public Result<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        leaveType.setId(id);
        return Result.success(leaveTypeRepository.save(leaveType));
    }

    @DeleteMapping("/leave-types/{id}")
    public Result<?> deleteLeaveType(@PathVariable Long id) {
        leaveTypeRepository.deleteById(id);
        return Result.success("删除成功");
    }

    // ===== 审批流程管理 =====
    @GetMapping("/flows")
    public Result<List<Map<String, Object>>> getFlows() {
        List<ApprovalFlowConfig> flows = flowConfigRepository.findAll();
        List<Map<String, Object>> list = flows.stream().map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("flowName", f.getFlowName());
            map.put("leaveTypeName", f.getLeaveType() != null ? f.getLeaveType().getTypeName() : "通用");
            map.put("leaveTypeId", f.getLeaveType() != null ? f.getLeaveType().getId() : null);
            map.put("minDays", f.getMinDays());
            map.put("maxDays", f.getMaxDays());
            map.put("status", f.getStatus());
            map.put("nodes", f.getNodes().stream().map(n -> {
                Map<String, Object> nMap = new HashMap<>();
                nMap.put("id", n.getId());
                nMap.put("nodeOrder", n.getNodeOrder());
                nMap.put("nodeName", n.getNodeName());
                nMap.put("approverRoleId", n.getApproverRole().getId());
                nMap.put("approverRoleName", n.getApproverRole().getRoleName());
                return nMap;
            }).collect(Collectors.toList()));
            return map;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @PostMapping("/flows")
    public Result<ApprovalFlowConfig> createFlow(@RequestBody ApprovalFlowConfig flow) {
        if (flow.getNodes() != null) {
            flow.getNodes().forEach(n -> n.setFlow(flow));
        }
        return Result.success(flowConfigRepository.save(flow));
    }

    @PutMapping("/flows/{id}")
    public Result<ApprovalFlowConfig> updateFlow(@PathVariable Long id, @RequestBody ApprovalFlowConfig flow) {
        ApprovalFlowConfig existing = flowConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("流程不存在"));
        existing.setFlowName(flow.getFlowName());
        existing.setLeaveType(flow.getLeaveType());
        existing.setMinDays(flow.getMinDays());
        existing.setMaxDays(flow.getMaxDays());
        existing.setStatus(flow.getStatus());
        existing.setDepartment(flow.getDepartment());
        // 清空旧节点，设置新节点
        flowNodeRepository.deleteAll(existing.getNodes());
        existing.getNodes().clear();
        if (flow.getNodes() != null) {
            flow.getNodes().forEach(n -> {
                n.setFlow(existing);
                n.setId(null);
            });
            existing.getNodes().addAll(flow.getNodes());
        }
        return Result.success(flowConfigRepository.save(existing));
    }

    @DeleteMapping("/flows/{id}")
    public Result<?> deleteFlow(@PathVariable Long id) {
        flowConfigRepository.deleteById(id);
        return Result.success("删除成功");
    }

    // ===== 院系管理 =====
    @GetMapping("/departments")
    public Result<List<Department>> getDepartments() {
        return Result.success(departmentRepository.findAll());
    }

    @PostMapping("/departments")
    public Result<Department> createDepartment(@RequestBody Department department) {
        return Result.success(departmentRepository.save(department));
    }

    @PutMapping("/departments/{id}")
    public Result<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        department.setId(id);
        return Result.success(departmentRepository.save(department));
    }

    // ===== 班级管理 =====
    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> getClasses(@RequestParam(required = false) Long departmentId) {
        List<ClassInfo> classes;
        if (departmentId != null) {
            classes = classInfoRepository.findByDepartmentId(departmentId);
        } else {
            classes = classInfoRepository.findAll();
        }
        List<Map<String, Object>> list = classes.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("className", c.getClassName());
            map.put("classCode", c.getClassCode());
            map.put("departmentName", c.getDepartment().getDeptName());
            map.put("grade", c.getGrade());
            map.put("status", c.getStatus());
            return map;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @PostMapping("/classes")
    public Result<ClassInfo> createClass(@RequestBody ClassInfo classInfo) {
        return Result.success(classInfoRepository.save(classInfo));
    }

    @PutMapping("/classes/{id}")
    public Result<ClassInfo> updateClass(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        classInfo.setId(id);
        return Result.success(classInfoRepository.save(classInfo));
    }

    @DeleteMapping("/classes/{id}")
    public Result<?> deleteClass(@PathVariable Long id) {
        classInfoRepository.deleteById(id);
        return Result.success("删除成功");
    }

    // ===== 辅导员-班级分配 =====
    @GetMapping("/counselor-classes/{counselorId}")
    public Result<List<Map<String, Object>>> getCounselorClasses(@PathVariable Long counselorId) {
        List<CounselorClass> list = counselorClassRepository.findByCounselorId(counselorId);
        List<Map<String, Object>> result = list.stream().map(cc -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cc.getId());
            map.put("classId", cc.getClassInfo().getId());
            map.put("className", cc.getClassInfo().getClassName());
            return map;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @PostMapping("/counselor-classes")
    public Result<?> assignCounselorClass(@RequestBody Map<String, Long> request) {
        Long counselorId = request.get("counselorId");
        Long classId = request.get("classId");

        SysUser counselor = userRepository.findById(counselorId)
                .orElseThrow(() -> new RuntimeException("辅导员不存在"));
        ClassInfo classInfo = classInfoRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("班级不存在"));

        CounselorClass cc = new CounselorClass();
        cc.setCounselor(counselor);
        cc.setClassInfo(classInfo);
        counselorClassRepository.save(cc);
        return Result.success("分配成功");
    }

    // ===== 销假规则 =====
    @GetMapping("/cancellation-rules")
    public Result<List<CancellationRule>> getCancellationRules() {
        return Result.success(cancellationRuleRepository.findAll());
    }

    @PostMapping("/cancellation-rules")
    public Result<CancellationRule> saveCancellationRule(@RequestBody CancellationRule rule) {
        return Result.success(cancellationRuleRepository.save(rule));
    }
}
