package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.DefectReportRequest;
import com.smartsubstation.service.IDefectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 缺陷控制器
 */
@Tag(name = "缺陷管理", description = "缺陷的上报、查询和处理")
@RestController
@RequestMapping("/defects")
@RequiredArgsConstructor
public class DefectController {

    private final IDefectService defectService;

    /**
     * 分页查询缺陷
     */
    @Operation(summary = "分页查询缺陷")
    @GetMapping("/page")
    public Result<PageResult<?>> pageQuery(PageQuery pageQuery) {
        return Result.success(defectService.pageQuery(pageQuery));
    }

    /**
     * 根据ID查询
     */
    @Operation(summary = "根据ID查询缺陷")
    @GetMapping("/{defectId}")
    public Result<?> getById(@PathVariable Long defectId) {
        return Result.success(defectService.getById(defectId));
    }

    /**
     * 上报缺陷
     */
    @Operation(summary = "上报缺陷")
    @PostMapping
    public Result<Long> report(@Valid @RequestBody DefectReportRequest request) {
        Long id = defectService.report(request);
        return Result.success(id);
    }

    /**
     * 确认缺陷
     */
    @Operation(summary = "确认缺陷")
    @PostMapping("/{defectId}/confirm")
    public Result<Void> confirm(@PathVariable Long defectId) {
        return Result.success(defectService.confirm(defectId));
    }

    /**
     * 更新缺陷状态
     */
    @Operation(summary = "更新缺陷状态")
    @PutMapping("/{defectId}/status")
    public Result<Void> updateStatus(
            @PathVariable Long defectId,
            @RequestParam String status) {
        return Result.success(defectService.updateStatus(defectId, status));
    }

    /**
     * 完成缺陷处理
     */
    @Operation(summary = "完成缺陷处理")
    @PostMapping("/{defectId}/complete")
    public Result<Void> complete(@PathVariable Long defectId) {
        return Result.success(defectService.complete(defectId));
    }
}
