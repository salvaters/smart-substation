package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.SubStationSaveRequest;
import com.smartsubstation.service.ISubStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 变电站控制器
 */
@Tag(name = "变电站管理", description = "变电站的增删改查操作")
@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class SubStationController {

    private final ISubStationService stationService;

    /**
     * 分页查询变电站
     */
    @Operation(summary = "分页查询变电站")
    @GetMapping("/page")
    public Result<PageResult<?>> pageQuery(PageQuery pageQuery) {
        return Result.success(stationService.pageQuery(pageQuery));
    }

    /**
     * 根据ID查询
     */
    @Operation(summary = "根据ID查询变电站")
    @GetMapping("/{stationId}")
    public Result<?> getById(@PathVariable Long stationId) {
        return Result.success(stationService.getById(stationId));
    }

    /**
     * 保存变电站
     */
    @Operation(summary = "保存变电站")
    @PostMapping
    public Result<Long> save(@Valid @RequestBody SubStationSaveRequest request) {
        Long id = stationService.save(request);
        return Result.success(id);
    }

    /**
     * 删除变电站
     */
    @Operation(summary = "删除变电站")
    @DeleteMapping("/{stationId}")
    public Result<Void> delete(@PathVariable Long stationId) {
        return Result.success(stationService.delete(stationId));
    }

    /**
     * 获取所有变电站列表
     */
    @Operation(summary = "获取所有变电站列表")
    @GetMapping("/list")
    public Result<java.util.List<?>> listAll() {
        return Result.success(stationService.listAll());
    }
}
