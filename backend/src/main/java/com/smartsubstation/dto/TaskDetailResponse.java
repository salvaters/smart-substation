package com.smartsubstation.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 任务详情响应
 */
@Data
public class TaskDetailResponse {

    private Long taskId;
    private String taskCode;
    private String title;
    private String description;
    private Long stationId;
    private String stationName;
    private Long templateId;
    private String templateName;
    private String inspectionType;
    private Long assigneeId;
    private String assigneeName;
    private String plannedStartTime;
    private String plannedEndTime;
    private String status;
    private Integer progress;
    private Integer deviceCount;
    private Integer completedCount;
    private Integer defectCount;
    private java.util.List<DeviceItem> devices;

    @Data
    public static class DeviceItem {
        private Long deviceId;
        private String deviceCode;
        private String deviceName;
        private String location;
        private String status;
    }
}
