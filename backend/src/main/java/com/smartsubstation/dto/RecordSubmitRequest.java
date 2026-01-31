package com.smartsubstation.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 巡检记录提交请求
 */
@Data
public class RecordSubmitRequest {

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "检查项ID不能为空")
    private Long itemId;

    private String checkValue;

    private String checkResult = "normal";

    private String description;

    private String photoUrls;
}
