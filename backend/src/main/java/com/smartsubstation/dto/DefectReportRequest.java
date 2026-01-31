package com.smartsubstation.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 缺陷上报请求
 */
@Data
public class DefectReportRequest {

    private Long defectId;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotBlank(message = "缺陷类型不能为空")
    private String defectType;

    @NotBlank(message = "缺陷描述不能为空")
    private String defectDescription;

    private String defectLevel;

    private String photoUrls;
}
