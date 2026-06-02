package com.leave.service;

import com.leave.common.BusinessException;
import com.leave.dto.LoginRequest;
import com.leave.dto.LoginResponse;
import com.leave.dto.RegisterRequest;
import com.leave.entity.ClassInfo;
import com.leave.entity.Department;
import com.leave.entity.SysRole;
import com.leave.entity.SysUser;
import com.leave.repository.*;
import com.leave.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ClassInfoRepository classInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public LoginResponse login(LoginRequest request) {
        SysUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被冻结，请联系管理员");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole().getRoleCode());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .roleCode(user.getRole().getRoleCode())
                .roleName(user.getRole().getRoleName())
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("该学号/工号已注册");
        }

        SysRole role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException("角色不存在"));

        if ("ADMIN".equals(role.getRoleCode())) {
            throw new BusinessException("不允许注册管理员账号");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setGender(request.getGender() != null ? request.getGender() : 0);
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(role);

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new BusinessException("院系不存在"));
            user.setDepartment(dept);
        }

        if (request.getClassId() != null) {
            ClassInfo classInfo = classInfoRepository.findById(request.getClassId())
                    .orElseThrow(() -> new BusinessException("班级不存在"));
            user.setClassInfo(classInfo);
        }

        userRepository.save(user);
    }
}
