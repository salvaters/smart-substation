package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.DeviceSaveRequest;
import com.smartsubstation.service.IDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 设备控制器
 */
@Tag(name = "设备管理", description = "设备的增删改查操作")
@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final IDeviceService deviceService;

    /**
     * 分页查询设备
     */
    @Operation(summary = "分页查询设备")
    @GetMapping("/page")
    public Result<PageResult<?>> pageQuery(PageQuery pageQuery) {
        return Result.success(deviceService.pageQuery(pageQuery));
    }

    /**
     * 根据ID查询设备
     */
    @Operation(summary = "根据ID查询设备")
    @GetMapping("/{deviceId}")
    public Result<?> getById(@PathVariable Long deviceId) {
        return Result.success(deviceService.getById(deviceId));
    }

    /**
     * 根据二维码查询设备
     */
    @Operation(summary = "根据二维码查询设备")
    @GetMapping("/qr/{qrCode}")
    public Result<?> getByQrCode(@PathVariable String qrCode) {
        return Result.success(deviceService.getByQrCode(qrCode));
    }

    /**
     * 保存设备
     */
    @Operation(summary = "保存设备")
    @PostMapping
    public Result<Long> save(@Valid @RequestBody DeviceSaveRequest request) {
        Long id = deviceService.save(request);
        return Result.success(id);
    }

    /**
     * 删除设备
     */
    @Operation(summary = "删除设备")
    @DeleteMapping("/{deviceId}")
    public Result<Void> delete(@PathVariable Long deviceId) {
        return Result.success(deviceService.delete(deviceId));
    }

    /**
     * 生成设备二维码
     */
    @Operation(summary = "生成设备二维码")
    @GetMapping("/{deviceId}/qrcode")
    public Result<String> generateQrCode(@PathVariable Long deviceId) {
        String qrCodeUrl = deviceService.generateQrCode(deviceId);
        return Result.success(qrCodeUrl);
    }
}
