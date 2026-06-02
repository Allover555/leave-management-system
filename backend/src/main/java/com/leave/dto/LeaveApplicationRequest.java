package com.leave.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class LeaveApplicationRequest {

    @NotNull(message = "请假类型不能为空")
    private Long leaveTypeId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    @NotBlank(message = "请假事由不能为空")
    private String reason;
}
