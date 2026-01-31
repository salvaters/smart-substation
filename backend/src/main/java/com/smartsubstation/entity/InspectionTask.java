package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 巡检任务实体
 */
@Data
@TableName("inspection_task")
public class InspectionTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "task_id", type = IdType.AUTO)
    private Long taskId;

    /**
     * 任务编码
     */
    private String taskCode;

    /**
     * 计划ID
     */
    private Long planId;

    /**
     * 变电站ID
     */
    private Long stationId;

    /**
     * 变电站名称(冗余)
     */
    private String stationName;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 模板名称(冗余)
     */
    private String templateName;

    /**
     * 巡检类型: daily-日巡, weekly-周巡, monthly-月巡, special-专项
     */
    private String inspectionType;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 执行人ID
     */
    private Long assigneeId;

    /**
     * 执行人姓名
     */
    private String assigneeName;

    /**
     * 计划开始时间
     */
    private LocalDateTime plannedStartTime;

    /**
     * 计划结束时间
     */
    private LocalDateTime plannedEndTime;

    /**
     * 实际开始时间
     */
    private LocalDateTime actualStartTime;

    /**
     * 实际结束时间
     */
    private LocalDateTime actualEndTime;

    /**
     * 状态: pending-待执行, in_progress-进行中, completed-已完成, overdue-已逾期, cancelled-已取消
     */
    private String status;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 设备数量
     */
    private Integer deviceCount;

    /**
     * 已完成设备数
     */
    private Integer completedCount;

    /**
     * 缺陷数量
     */
    private Integer defectCount;

    /**
     * 版本号(离线同步用)
     */
    private Integer version;

    /**
     * 是否离线创建: 1-是, 0-否
     */
    private Integer isOffline;

    /**
     * 离线创建时间
     */
    private LocalDateTime offlineCreateTime;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncTime;

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
     * 删除标记: 0-未删除, 1-已删除
     */
    @TableLogic
    private Integer deleted;
}
