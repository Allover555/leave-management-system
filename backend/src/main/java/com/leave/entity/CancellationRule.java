package com.leave.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cancellation_rule")
public class CancellationRule extends BaseEntity {

    @Column(name = "remind_hours")
    private Integer remindHours = 24;

    @Column(name = "overdue_hours")
    private Integer overdueHours = 48;

    @Column(name = "penalty_desc")
    private String penaltyDesc;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
}
