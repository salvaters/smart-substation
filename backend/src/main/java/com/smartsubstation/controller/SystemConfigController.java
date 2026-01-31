package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.service.ISystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 */
@Tag(name = "系统配置", description = "系统配置的查询和更新")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final ISystemConfigService configService;

    /**
     * 获取配置值
     */
    @Operation(summary = "获取配置值")
    @GetMapping("/{configKey}")
    public Result<String> getConfig(@PathVariable String configKey) {
        return Result.success(configService.getConfig(configKey));
    }

    /**
     * 更新配置
     */
    @Operation(summary = "更新配置")
    @PutMapping("/{configKey}")
    public Result<Void> updateConfig(
            @PathVariable String configKey,
            @RequestParam String configValue) {
        return Result.success(configService.updateConfig(configKey, configValue));
    }

    /**
     * 获取所有配置
     */
    @Operation(summary = "获取所有配置")
    @GetMapping
    public Result<List<?>> getAllConfigs() {
        return Result.success(configService.getAllConfigs());
    }

    /**
     * 按分组获取配置
     */
    @Operation(summary = "按分组获取配置")
    @GetMapping("/group/{groupName}")
    public Result<Map<String, String>> getConfigsByGroup(@PathVariable String groupName) {
        return Result.success(configService.getConfigsByGroup(groupName));
    }
}
