package com.smartsubstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 变电站实体
 */
@Data
@TableName("sub_station")
public class SubStation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "station_id", type = IdType.AUTO)
    private Long stationId;

    /**
     * 变电站编码
     */
    private String stationCode;

    /**
     * 变电站名称
     */
    private String stationName;

    /**
     * 变电站类型
     */
    private String stationType;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 容量
     */
    private String capacity;

    /**
     * 电压等级
     */
    private String voltageLevel;

    /**
     * 运维单位
     */
    private String operator;

    /**
     * 负责人
     */
    private String responsiblePerson;

    /**
     * 负责人电话
     */
    private String responsiblePhone;

    /**
     * 状态: 1-运行中, 2-停运, 3-检修中
     */
    private Integer status;

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
