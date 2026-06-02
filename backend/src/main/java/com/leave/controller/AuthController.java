package com.leave.controller;

import com.leave.common.Result;
import com.leave.dto.LoginRequest;
import com.leave.dto.LoginResponse;
import com.leave.dto.RegisterRequest;
import com.leave.entity.ClassInfo;
import com.leave.entity.Department;
import com.leave.entity.SysRole;
import com.leave.repository.ClassInfoRepository;
import com.leave.repository.DepartmentRepository;
import com.leave.repository.SysRoleRepository;
import com.leave.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysRoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ClassInfoRepository classInfoRepository;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }

    @GetMapping("/roles")
    public Result<List<Map<String, Object>>> getRoles() {
        List<SysRole> roles = roleRepository.findAll();
        List<Map<String, Object>> list = roles.stream()
                .filter(r -> !"ADMIN".equals(r.getRoleCode()))
                .map(r -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", r.getId());
                    map.put("roleName", r.getRoleName());
                    map.put("roleCode", r.getRoleCode());
                    return map;
                }).collect(Collectors.toList());
        return Result.success(list);
    }

    @GetMapping("/departments")
    public Result<List<Map<String, Object>>> getDepartments() {
        List<Department> depts = departmentRepository.findByStatus(1);
        List<Map<String, Object>> list = depts.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("deptName", d.getDeptName());
            map.put("deptCode", d.getDeptCode());
            return map;
        }).collect(Collectors.toList());
        return Result.success(list);
    }

    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> getClasses(@RequestParam(required = false) Long departmentId) {
        List<ClassInfo> classes;
        if (departmentId != null) {
            classes = classInfoRepository.findByDepartmentId(departmentId);
        } else {
            classes = classInfoRepository.findByStatus(1);
        }
        List<Map<String, Object>> list = classes.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("className", c.getClassName());
            map.put("classCode", c.getClassCode());
            map.put("departmentId", c.getDepartment().getId());
            return map;
        }).collect(Collectors.toList());
        return Result.success(list);
    }
}
