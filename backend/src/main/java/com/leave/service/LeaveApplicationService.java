package com.leave.service;

import com.leave.common.BusinessException;
import com.leave.common.PageResult;
import com.leave.dto.*;
import com.leave.entity.*;
import com.leave.repository.*;
import com.leave.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ApprovalFlowConfigRepository flowConfigRepository;
    private final ApprovalFlowNodeRepository flowNodeRepository;
    private final ApprovalRecordRepository approvalRecordRepository;
    private final SysUserRepository userRepository;
    private final CounselorClassRepository counselorClassRepository;
    private final LeaveCancellationRepository cancellationRepository;
    private final LeaveAttachmentRepository attachmentRepository;
    private final MessageService messageService;

    @Transactional
    public LeaveApplicationDTO createApplication(LeaveApplicationRequest request, MultipartFile[] files) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser applicant = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new BusinessException("请假类型不存在"));

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        long hours = ChronoUnit.HOURS.between(request.getStartTime(), request.getEndTime());
        BigDecimal duration = BigDecimal.valueOf(hours).divide(BigDecimal.valueOf(24), 1, RoundingMode.HALF_UP);
        if (duration.compareTo(BigDecimal.ZERO) <= 0) {
            duration = BigDecimal.valueOf(0.5);
        }

        if (leaveType.getMaxDays() > 0 && duration.intValue() > leaveType.getMaxDays()) {
            throw new BusinessException("超过" + leaveType.getTypeName() + "最大请假天数(" + leaveType.getMaxDays() + "天)");
        }

        List<ApprovalFlowConfig> flows = flowConfigRepository.findMatchingFlow(leaveType.getId(), duration.intValue());
        if (flows.isEmpty()) {
            flows = flowConfigRepository.findMatchingFlow(null, duration.intValue());
        }
        if (flows.isEmpty()) {
            throw new BusinessException("未找到匹配的审批流程，请联系管理员");
        }

        ApprovalFlowConfig flow = flows.get(0);
        List<ApprovalFlowNode> nodes = flowNodeRepository.findByFlowIdOrderByNodeOrderAsc(flow.getId());
        if (nodes.isEmpty()) {
            throw new BusinessException("审批流程未配置审批节点");
        }

        LeaveApplication application = new LeaveApplication();
        application.setLeaveNo(generateLeaveNo());
        application.setApplicant(applicant);
        application.setLeaveType(leaveType);
        application.setStartTime(request.getStartTime());
        application.setEndTime(request.getEndTime());
        application.setDuration(duration);
        application.setReason(request.getReason());
        application.setStatus(0);
        application.setFlow(flow);
        application.setCurrentNodeId(nodes.get(0).getId());

        leaveRepository.save(application);

        if (files != null && files.length > 0) {
            saveAttachments(application, files);
        }

        notifyApprovers(application, nodes.get(0));

        return convertToDTO(application);
    }

    public PageResult<LeaveApplicationDTO> getMyApplications(Integer status, int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<LeaveApplication> appPage;
        if (status != null) {
            appPage = leaveRepository.findByApplicantIdAndStatus(userId, status, PageRequest.of(page - 1, size));
        } else {
            appPage = leaveRepository.findByApplicantId(userId, PageRequest.of(page - 1, size));
        }
        List<LeaveApplicationDTO> dtos = appPage.getContent().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return PageResult.of(appPage, dtos);
    }

    public LeaveApplicationDTO getApplicationDetail(Long id) {
        LeaveApplication app = leaveRepository.findById(id)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));
        return convertToDTO(app);
    }

    @Transactional
    public void cancelApplication(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        LeaveApplication app = leaveRepository.findById(id)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));

        if (!app.getApplicant().getId().equals(userId)) {
            throw new BusinessException("只能撤销自己的请假申请");
        }

        if (app.getStatus() != 0) {
            throw new BusinessException("只能撤销待审批的申请");
        }

        app.setStatus(4);
        app.setCancelTime(LocalDateTime.now());
        leaveRepository.save(app);
    }

    @Transactional
    public void approveApplication(Long id, ApprovalRequest request) {
        Long approverId = SecurityUtils.getCurrentUserId();
        SysUser approver = userRepository.findById(approverId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        LeaveApplication app = leaveRepository.findById(id)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));

        if (app.getStatus() != 0 && app.getStatus() != 1) {
            throw new BusinessException("该申请当前状态不可审批");
        }

        ApprovalFlowNode currentNode = flowNodeRepository.findById(app.getCurrentNodeId())
                .orElseThrow(() -> new BusinessException("审批节点不存在"));

        String approverRole = approver.getRole().getRoleCode();
        if (!"ADMIN".equals(approverRole) && !currentNode.getApproverRole().getRoleCode().equals(approverRole)) {
            throw new BusinessException("您无权审批此申请");
        }

        if ("COUNSELOR".equals(approverRole)) {
            List<Long> classIds = counselorClassRepository.findClassIdsByCounselorId(approverId);
            if (app.getApplicant().getClassInfo() != null && !classIds.contains(app.getApplicant().getClassInfo().getId())) {
                throw new BusinessException("您不是该学生所在班级的辅导员");
            }
        }

        ApprovalRecord record = new ApprovalRecord();
        record.setLeaveApplication(app);
        record.setNodeId(currentNode.getId());
        record.setApprover(approver);
        record.setAction(request.getAction());
        record.setComment(request.getComment());
        approvalRecordRepository.save(record);

        if (request.getAction() == 2) {
            app.setStatus(3);
            leaveRepository.save(app);
            messageService.sendMessage(app.getApplicant().getId(),
                    "请假申请已驳回", "您的请假申请(" + app.getLeaveNo() + ")已被驳回，原因：" + request.getComment(),
                    1, app.getId());
            return;
        }

        List<ApprovalFlowNode> nodes = flowNodeRepository.findByFlowIdOrderByNodeOrderAsc(app.getFlow().getId());
        int currentIndex = -1;
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getId().equals(currentNode.getId())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex < nodes.size() - 1) {
            ApprovalFlowNode nextNode = nodes.get(currentIndex + 1);
            app.setCurrentNodeId(nextNode.getId());
            app.setStatus(1);
            leaveRepository.save(app);
            notifyApprovers(app, nextNode);
            messageService.sendMessage(app.getApplicant().getId(),
                    "请假审批进度更新", "您的请假申请(" + app.getLeaveNo() + ")已通过" + currentNode.getNodeName() + "，等待" + nextNode.getNodeName(),
                    1, app.getId());
        } else {
            app.setStatus(2);
            app.setCurrentNodeId(null);
            leaveRepository.save(app);
            messageService.sendMessage(app.getApplicant().getId(),
                    "请假申请已通过", "您的请假申请(" + app.getLeaveNo() + ")已全部审批通过",
                    1, app.getId());
        }
    }

    public PageResult<LeaveApplicationDTO> getPendingApprovals(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        String role = SecurityUtils.getCurrentRole();

        Page<LeaveApplication> appPage;
        if ("ADMIN".equals(role) || "DEPT_ADMIN".equals(role)) {
            appPage = leaveRepository.findPendingByApproverRole(role, PageRequest.of(page - 1, size));
        } else {
            List<Long> classIds = counselorClassRepository.findClassIdsByCounselorId(userId);
            if (classIds.isEmpty()) {
                return PageResult.of(Page.empty(), Collections.emptyList());
            }
            appPage = leaveRepository.findPendingByClassIds(classIds, PageRequest.of(page - 1, size));
        }

        List<LeaveApplicationDTO> dtos = appPage.getContent().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return PageResult.of(appPage, dtos);
    }

    public PageResult<LeaveApplicationDTO> getAllApplications(Integer status, int page, int size) {
        Page<LeaveApplication> appPage;
        if (status != null) {
            appPage = leaveRepository.findByStatus(status, PageRequest.of(page - 1, size));
        } else {
            appPage = leaveRepository.findAllOrdered(PageRequest.of(page - 1, size));
        }
        List<LeaveApplicationDTO> dtos = appPage.getContent().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return PageResult.of(appPage, dtos);
    }

    public PageResult<LeaveApplicationDTO> getClassApplications(Integer status, int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> classIds = counselorClassRepository.findClassIdsByCounselorId(userId);
        if (classIds.isEmpty()) {
            return PageResult.of(Page.empty(), Collections.emptyList());
        }
        Page<LeaveApplication> appPage = leaveRepository.findByClassIds(classIds, PageRequest.of(page - 1, size));
        List<LeaveApplicationDTO> dtos = appPage.getContent().stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return PageResult.of(appPage, dtos);
    }

    @Transactional
    public void submitCancellation(Long leaveId, CancellationRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        LeaveApplication app = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new BusinessException("请假申请不存在"));

        if (!app.getApplicant().getId().equals(userId)) {
            throw new BusinessException("只能对自己的请假进行销假");
        }
        if (app.getStatus() != 2) {
            throw new BusinessException("只有已通过的请假才能销假");
        }

        if (cancellationRepository.findByLeaveApplicationId(leaveId).isPresent()) {
            throw new BusinessException("已提交销假申请，请勿重复提交");
        }

        LeaveCancellation cancellation = new LeaveCancellation();
        cancellation.setLeaveApplication(app);
        cancellation.setApplicant(app.getApplicant());
        cancellation.setActualReturnTime(request.getActualReturnTime());
        cancellation.setIsOverdue(request.getActualReturnTime().isAfter(app.getEndTime()) ? 1 : 0);
        cancellationRepository.save(cancellation);
    }

    @Transactional
    public void reviewCancellation(Long cancellationId, CancellationReviewRequest request) {
        Long reviewerId = SecurityUtils.getCurrentUserId();
        SysUser reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        LeaveCancellation cancellation = cancellationRepository.findById(cancellationId)
                .orElseThrow(() -> new BusinessException("销假申请不存在"));

        if (cancellation.getStatus() != 0) {
            throw new BusinessException("该销假申请已处理");
        }

        cancellation.setStatus(request.getAction());
        cancellation.setReviewer(reviewer);
        cancellation.setReviewComment(request.getComment());
        cancellation.setReviewedAt(LocalDateTime.now());
        cancellationRepository.save(cancellation);

        if (request.getAction() == 1) {
            LeaveApplication app = cancellation.getLeaveApplication();
            app.setStatus(5);
            leaveRepository.save(app);
            messageService.sendMessage(cancellation.getApplicant().getId(),
                    "销假已通过", "您的销假申请已通过", 2, app.getId());
        } else {
            messageService.sendMessage(cancellation.getApplicant().getId(),
                    "销假已驳回", "您的销假申请已被驳回，原因：" + request.getComment(), 2, cancellation.getLeaveApplication().getId());
        }
    }

    private void saveAttachments(LeaveApplication application, MultipartFile[] files) throws IOException {
        Path uploadDir = Paths.get("./uploads/attachments");
        Files.createDirectories(uploadDir);

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            LeaveAttachment attachment = new LeaveAttachment();
            attachment.setLeaveApplication(application);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFilePath("/files/attachments/" + fileName);
            attachment.setFileSize(file.getSize());
            attachment.setFileType(file.getContentType());
            attachmentRepository.save(attachment);
        }
    }

    private void notifyApprovers(LeaveApplication app, ApprovalFlowNode node) {
        String roleCode = node.getApproverRole().getRoleCode();
        if ("COUNSELOR".equals(roleCode) && app.getApplicant().getClassInfo() != null) {
            // do nothing special, just get counselors for this class
        }
        List<SysUser> approvers = userRepository.findByRoleCode(roleCode);
        for (SysUser approver : approvers) {
            messageService.sendMessage(approver.getId(),
                    "新的请假审批", app.getApplicant().getRealName() + "提交了请假申请，请及时处理",
                    1, app.getId());
        }
    }

    private String generateLeaveNo() {
        return "LV" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + String.format("%04d", new Random().nextInt(10000));
    }

    public LeaveApplicationDTO convertToDTO(LeaveApplication app) {
        LeaveApplicationDTO dto = new LeaveApplicationDTO();
        dto.setId(app.getId());
        dto.setLeaveNo(app.getLeaveNo());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setApplicantName(app.getApplicant().getRealName());
        dto.setApplicantUsername(app.getApplicant().getUsername());

        if (app.getApplicant().getClassInfo() != null) {
            dto.setClassName(app.getApplicant().getClassInfo().getClassName());
        }
        if (app.getApplicant().getDepartment() != null) {
            dto.setDepartmentName(app.getApplicant().getDepartment().getDeptName());
        }

        dto.setLeaveTypeId(app.getLeaveType().getId());
        dto.setLeaveTypeName(app.getLeaveType().getTypeName());
        dto.setStartTime(app.getStartTime());
        dto.setEndTime(app.getEndTime());
        dto.setDuration(app.getDuration());
        dto.setReason(app.getReason());
        dto.setStatus(app.getStatus());
        dto.setStatusText(getStatusText(app.getStatus()));
        dto.setCancelTime(app.getCancelTime());
        dto.setCancelReason(app.getCancelReason());
        dto.setCreatedAt(app.getCreatedAt());

        if (app.getCurrentNodeId() != null) {
            flowNodeRepository.findById(app.getCurrentNodeId()).ifPresent(node ->
                    dto.setCurrentNodeName(node.getNodeName()));
        }

        List<LeaveAttachment> attachments = attachmentRepository.findByLeaveApplicationId(app.getId());
        dto.setAttachments(attachments.stream().map(a -> {
            AttachmentDTO adto = new AttachmentDTO();
            adto.setId(a.getId());
            adto.setFileName(a.getFileName());
            adto.setFilePath(a.getFilePath());
            adto.setFileSize(a.getFileSize());
            adto.setFileType(a.getFileType());
            return adto;
        }).collect(Collectors.toList()));

        List<ApprovalRecord> records = approvalRecordRepository.findByLeaveApplicationIdOrderByApprovedAtAsc(app.getId());
        dto.setApprovalRecords(records.stream().map(r -> {
            ApprovalRecordDTO rdto = new ApprovalRecordDTO();
            rdto.setId(r.getId());
            rdto.setApproverId(r.getApprover().getId());
            rdto.setApproverName(r.getApprover().getRealName());
            rdto.setAction(r.getAction());
            rdto.setActionText(r.getAction() == 1 ? "通过" : "驳回");
            rdto.setComment(r.getComment());
            rdto.setApprovedAt(r.getApprovedAt());
            if (r.getNodeId() != null) {
                flowNodeRepository.findById(r.getNodeId()).ifPresent(node ->
                        rdto.setNodeName(node.getNodeName()));
            }
            return rdto;
        }).collect(Collectors.toList()));

        return dto;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待审批";
            case 1: return "审批中";
            case 2: return "已通过";
            case 3: return "已驳回";
            case 4: return "已撤销";
            case 5: return "已销假";
            default: return "未知";
        }
    }
}
