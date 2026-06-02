package com.leave.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CancellationRequest {

    @NotNull(message = "实际返校时间不能为空")
    private LocalDateTime actualReturnTime;
}
