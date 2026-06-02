package com.leave.controller;

import com.leave.common.PageResult;
import com.leave.common.Result;
import com.leave.dto.ChangePasswordRequest;
import com.leave.dto.UserDTO;
import com.leave.dto.UserUpdateRequest;
import com.leave.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPT_ADMIN')")
    public Result<PageResult<UserDTO>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleCode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        String kw = (keyword != null && keyword.trim().isEmpty()) ? null : keyword;
        String rc = (roleCode != null && roleCode.trim().isEmpty()) ? null : roleCode;
        return Result.success(userService.searchUsers(kw, rc, page, size));
    }

    @PutMapping("/update")
    public Result<UserDTO> updateCurrentUser(@RequestBody UserUpdateRequest request) {
        Long userId = com.leave.security.SecurityUtils.getCurrentUserId();
        return Result.success(userService.updateUser(userId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPT_ADMIN')")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return Result.success(userService.updateUser(id, request));
    }

    @PostMapping("/password")
    public Result<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return Result.success("密码修改成功");
    }

    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(userService.uploadAvatar(file));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}
