package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备实体
 */
@Data
@TableName("device_info")
public class DeviceInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_id", type = IdType.AUTO)
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
     * 设备型号
     */
    private String deviceModel;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 设备分类ID
     */
    private Long categoryId;

    /**
     * 所属变电站ID
     */
    private Long stationId;

    /**
     * 安装位置
     */
    private String location;

    /**
     * 二维码内容(唯一标识)
     */
    private String qrCode;

    /**
     * 二维码图片URL
     */
    private String qrCodeUrl;

    /**
     * 额定电压
     */
    private String ratedVoltage;

    /**
     * 额定电流
     */
    private String ratedCurrent;

    /**
     * 额定容量
     */
    private String ratedCapacity;

    /**
     * 投运日期
     */
    private LocalDate commissioningDate;

    /**
     * 质保到期日
     */
    private LocalDate warrantyExpireDate;

    /**
     * 状态: 1-正常, 2-故障, 3-检修, 4-停用
     */
    private Integer status;

    /**
     * 版本号(离线同步用)
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
