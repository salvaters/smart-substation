package com.smartsubstation.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 变电站保存请求
 */
@Data
public class SubStationSaveRequest {

    private Long stationId;

    @NotBlank(message = "变电站编码不能为空")
    private String stationCode;

    @NotBlank(message = "变电站名称不能为空")
    private String stationName;

    private String stationType;

    private String address;

    private Double longitude;

    private Double latitude;

    private String capacity;

    private String voltageLevel;

    private String operator;

    private String responsiblePerson;

    private String responsiblePhone;

    private Integer status = 1;
}
