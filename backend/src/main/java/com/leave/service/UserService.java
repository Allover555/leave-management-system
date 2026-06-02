package com.leave.service;

import com.leave.common.BusinessException;
import com.leave.common.PageResult;
import com.leave.dto.ChangePasswordRequest;
import com.leave.dto.UserDTO;
import com.leave.dto.UserUpdateRequest;
import com.leave.entity.ClassInfo;
import com.leave.entity.Department;
import com.leave.entity.SysRole;
import com.leave.entity.SysUser;
import com.leave.repository.*;
import com.leave.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SysUserRepository userRepository;
    private final SysRoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ClassInfoRepository classInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToDTO(user);
    }

    public UserDTO getUserById(Long id) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToDTO(user);
    }

    public PageResult<UserDTO> searchUsers(String keyword, String roleCode, int page, int size) {
        Page<SysUser> userPage = userRepository.searchUsers(keyword, roleCode, PageRequest.of(page - 1, size));
        List<UserDTO> dtos = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return PageResult.of(userPage, dtos);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UserUpdateRequest request) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (request.getRealName() != null) user.setRealName(request.getRealName());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getStatus() != null) user.setStatus(request.getStatus());

        if (request.getRoleId() != null) {
            SysRole role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new BusinessException("角色不存在"));
            user.setRole(role);
        }

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
        return convertToDTO(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get("./uploads/avatars");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());

        String avatarPath = "/files/avatars/" + fileName;
        user.setAvatar(avatarPath);
        userRepository.save(user);

        return avatarPath;
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(userId);
    }

    public UserDTO convertToDTO(SysUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setGender(user.getGender());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setRoleCode(user.getRole().getRoleCode());
        dto.setRoleName(user.getRole().getRoleName());
        dto.setStatus(user.getStatus());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());

        if (user.getDepartment() != null) {
            dto.setDepartmentId(user.getDepartment().getId());
            dto.setDepartmentName(user.getDepartment().getDeptName());
        }
        if (user.getClassInfo() != null) {
            dto.setClassId(user.getClassInfo().getId());
            dto.setClassName(user.getClassInfo().getClassName());
        }

        return dto;
    }
}
