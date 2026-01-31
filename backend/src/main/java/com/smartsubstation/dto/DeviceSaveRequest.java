package com.smartsubstation.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 设备保存请求
 */
@Data
public class DeviceSaveRequest {

    private Long deviceId;

    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    private String deviceModel;

    private String manufacturer;

    @NotNull(message = "设备分类不能为空")
    private Long categoryId;

    @NotNull(message = "所属变电站不能为空")
    private Long stationId;

    private String location;

    private String ratedVoltage;

    private String ratedCurrent;

    private String ratedCapacity;

    private Integer status = 1;
}
