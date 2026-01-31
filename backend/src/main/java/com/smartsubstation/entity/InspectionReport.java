package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 巡检报告实体
 */
@Data
@TableName("inspection_report")
public class InspectionReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "report_id", type = IdType.AUTO)
    private Long reportId;

    /**
     * 报告编号
     */
    private String reportCode;

    /**
     * 报告类型
     */
    private String reportType;

    /**
     * 关联任务ID
     */
    private Long taskId;

    /**
     * 变电站ID
     */
    private Long stationId;

    /**
     * 变电站名称
     */
    private String stationName;

    /**
     * 报告周期
     */
    private String reportPeriod;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 巡检总结
     */
    private String summary;

    /**
     * 统计数据
     */
    private String statistics;

    /**
     * 设备总数
     */
    private Integer deviceCount;

    /**
     * 正常数量
     */
    private Integer normalCount;

    /**
     * 异常数量
     */
    private Integer abnormalCount;

    /**
     * 缺陷数量
     */
    private Integer defectCount;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 状态
     */
    private String status;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;

    /**
     * 审核人ID
     */
    private Long approverId;

    /**
     * 审核人姓名
     */
    private String approverName;

    /**
     * 审核时间
     */
    private LocalDateTime approveTime;

    /**
     * 审核意见
     */
    private String approveComment;

    /**
     * 报告文件URL
     */
    private String fileUrl;

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
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
