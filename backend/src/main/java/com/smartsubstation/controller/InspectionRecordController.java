package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.RecordSubmitRequest;
import com.smartsubstation.service.IInspectionRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 巡检记录控制器
 */
@Tag(name = "巡检记录管理", description = "巡检记录的提交和查询")
@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class InspectionRecordController {

    private final IInspectionRecordService recordService;

    /**
     * 提交巡检记录
     */
    @Operation(summary = "提交巡检记录")
    @PostMapping("/submit")
    public Result<Long> submit(@Valid @RequestBody RecordSubmitRequest request) {
        Long id = recordService.submit(request);
        return Result.success(id);
    }

    /**
     * 批量提交记录
     */
    @Operation(summary = "批量提交记录")
    @PostMapping("/batch")
    public Result<List<Long>> batchSubmit(@Valid @RequestBody List<RecordSubmitRequest> requests) {
        List<Long> ids = recordService.batchSubmit(requests);
        return Result.success(ids);
    }

    /**
     * 查询任务的巡检记录
     */
    @Operation(summary = "查询任务的巡检记录")
    @GetMapping("/task/{taskId}")
    public Result<PageResult<?>> getRecordsByTask(
            @PathVariable Long taskId,
            PageQuery pageQuery) {
        return Result.success(recordService.getRecordsByTask(taskId, pageQuery));
    }
}
