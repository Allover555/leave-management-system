package com.leave.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CancellationReviewRequest {

    @NotNull(message = "审核动作不能为空")
    private Integer action; // 1通过 2驳回

    private String comment;
}
