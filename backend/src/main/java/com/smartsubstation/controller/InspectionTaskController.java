package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.TaskDetailResponse;
import com.smartsubstation.service.IInspectionTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 巡检任务控制器
 */
@Tag(name = "巡检任务管理", description = "巡检任务的查询和管理")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class InspectionTaskController {

    private final IInspectionTaskService taskService;

    /**
     * 分页查询任务
     */
    @Operation(summary = "分页查询任务")
    @GetMapping("/page")
    public Result<PageResult<?>> pageQuery(PageQuery pageQuery) {
        return Result.success(taskService.pageQuery(pageQuery));
    }

    /**
     * 查询任务详情
     */
    @Operation(summary = "查询任务详情")
    @GetMapping("/{taskId}")
    public Result<TaskDetailResponse> getDetailById(@PathVariable Long taskId) {
        return Result.success((TaskDetailResponse) taskService.getDetailById(taskId));
    }

    /**
     * 获取我的任务
     */
    @Operation(summary = "获取我的任务列表")
    @GetMapping("/my")
    public Result<PageResult<?>> getMyTasks(PageQuery pageQuery) {
        return Result.success(taskService.getMyTasks(pageQuery));
    }

    /**
     * 开始任务
     */
    @Operation(summary = "开始任务")
    @PostMapping("/{taskId}/start")
    public Result<Void> startTask(@PathVariable Long taskId) {
        return Result.success(taskService.startTask(taskId));
    }

    /**
     * 完成任务
     */
    @Operation(summary = "完成任务")
    @PostMapping("/{taskId}/complete")
    public Result<Void> completeTask(@PathVariable Long taskId) {
        return Result.success(taskService.completeTask(taskId));
    }

    /**
     * 取消任务
     */
    @Operation(summary = "取消任务")
    @PostMapping("/{taskId}/cancel")
    public Result<Void> cancelTask(@PathVariable Long taskId) {
        return Result.success(taskService.cancelTask(taskId));
    }
}
