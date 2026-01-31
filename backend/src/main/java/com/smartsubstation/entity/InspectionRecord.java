package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 巡检记录实体
 */
@Data
@TableName("inspection_record")
public class InspectionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;

    /**
     * 记录编码
     */
    private String recordCode;

    /**
     * 任务ID
     */
    private Long taskId;

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
     * 检查项ID
     */
    private Long itemId;

    /**
     * 检查项名称
     */
    private String itemName;

    /**
     * 检查项类型
     */
    private String itemType;

    /**
     * 检查值
     */
    private String checkValue;

    /**
     * 检查结果
     */
    private String checkResult;

    /**
     * 照片URL
     */
    private String photoUrls;

    /**
     * 描述说明
     */
    private String description;

    /**
     * 关联缺陷ID
     */
    private Long defectId;

    /**
     * 检查人ID
     */
    private Long inspectorId;

    /**
     * 检查人姓名
     */
    private String inspectorName;

    /**
     * 检查时间
     */
    private LocalDateTime checkTime;

    /**
     * 是否离线记录
     */
    private Integer isOffline;

    /**
     * 离线创建时间
     */
    private LocalDateTime offlineCreateTime;

    /**
     * 版本号
     */
    private Integer version;

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
     * 删除标记
     */
    @TableLogic
    private Integer deleted;
}
