package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 巡检计划实体
 */
@Data
@TableName("inspection_plan")
public class InspectionPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "plan_id", type = IdType.AUTO)
    private Long planId;

    /**
     * 计划编码
     */
    private String planCode;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 变电站ID
     */
    private Long stationId;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 巡检类型
     */
    private String inspectionType;

    /**
     * 周期类型
     */
    private String cycleType;

    /**
     * 周期值
     */
    private Integer cycleValue;

    /**
     * 开始日期
     */
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    private LocalDateTime endDate;

    /**
     * 执行人ID
     */
    private Long assigneeId;

    /**
     * 执行人姓名
     */
    private String assigneeName;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核人姓名
     */
    private String reviewerName;

    /**
     * 状态
     */
    private String status;

    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecuteTime;

    /**
     * 下次执行时间
     */
    private LocalDateTime nextExecuteTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
