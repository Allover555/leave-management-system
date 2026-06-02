package com.leave.controller;

import com.leave.common.PageResult;
import com.leave.common.Result;
import com.leave.dto.*;
import com.leave.entity.LeaveType;
import com.leave.repository.LeaveTypeRepository;
import com.leave.service.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveApplicationService leaveService;
    private final LeaveTypeRepository leaveTypeRepository;

    @GetMapping("/types")
    public Result<java.util.List<LeaveType>> getLeaveTypes() {
        return Result.success(leaveTypeRepository.findByStatus(1));
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<LeaveApplicationDTO> apply(
            @Valid @RequestPart("data") LeaveApplicationRequest request,
            @RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {
        return Result.success(leaveService.createApplication(request, files));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<LeaveApplicationDTO>> myApplications(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(leaveService.getMyApplications(status, page, size));
    }

    @GetMapping("/{id}")
    public Result<LeaveApplicationDTO> getDetail(@PathVariable Long id) {
        return Result.success(leaveService.getApplicationDetail(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> cancel(@PathVariable Long id) {
        leaveService.cancelApplication(id);
        return Result.success("撤销成功");
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN', 'ADMIN')")
    public Result<?> approve(@PathVariable Long id, @Valid @RequestBody ApprovalRequest request) {
        leaveService.approveApplication(id, request);
        return Result.success("审批完成");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN', 'ADMIN')")
    public Result<PageResult<LeaveApplicationDTO>> pendingApprovals(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(leaveService.getPendingApprovals(page, size));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN', 'ADMIN')")
    public Result<PageResult<LeaveApplicationDTO>> allApplications(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(leaveService.getAllApplications(status, page, size));
    }

    @GetMapping("/class")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN')")
    public Result<PageResult<LeaveApplicationDTO>> classApplications(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(leaveService.getClassApplications(status, page, size));
    }

    @PostMapping("/{leaveId}/cancellation")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> submitCancellation(@PathVariable Long leaveId, @Valid @RequestBody CancellationRequest request) {
        leaveService.submitCancellation(leaveId, request);
        return Result.success("销假申请已提交");
    }

    @PostMapping("/cancellation/{id}/review")
    @PreAuthorize("hasAnyRole('COUNSELOR', 'DEPT_ADMIN', 'ADMIN')")
    public Result<?> reviewCancellation(@PathVariable Long id, @Valid @RequestBody CancellationReviewRequest request) {
        leaveService.reviewCancellation(id, request);
        return Result.success("审核完成");
    }
}
