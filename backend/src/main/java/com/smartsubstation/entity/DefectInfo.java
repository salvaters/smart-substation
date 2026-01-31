package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 缺陷实体
 */
@Data
@TableName("defect_info")
public class DefectInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "defect_id", type = IdType.AUTO)
    private Long defectId;

    /**
     * 缺陷编号
     */
    private String defectCode;

    /**
     * 变电站ID
     */
    private Long stationId;

    /**
     * 变电站名称
     */
    private String stationName;

    /**
     * 设备ID
     */
    private Long deviceId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 关联巡检记录ID
     */
    private Long recordId;

    /**
     * 缺陷类型
     */
    private String defectType;

    /**
     * 缺陷描述
     */
    private String defectDescription;

    /**
     * 缺陷等级
     */
    private String defectLevel;

    /**
     * 照片URL
     */
    private String photoUrls;

    /**
     * 发现人ID
     */
    private Long discovererId;

    /**
     * 发现人姓名
     */
    private String discovererName;

    /**
     * 发现时间
     */
    private LocalDateTime discoverTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 确认人ID
     */
    private Long confirmedBy;

    /**
     * 确认时间
     */
    private LocalDateTime confirmedTime;

    /**
     * 整改方案
     */
    private String repairPlan;

    /**
     * 整改人
     */
    private String repairPerson;

    /**
     * 整改期限
     */
    private LocalDate repairDeadline;

    /**
     * 实际整改时间
     */
    private LocalDateTime actualRepairTime;

    /**
     * 整改说明
     */
    private String repairDescription;

    /**
     * 整改照片URL
     */
    private String repairPhotos;

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
