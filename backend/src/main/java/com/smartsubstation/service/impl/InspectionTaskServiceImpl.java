package com.smartsubstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartsubstation.common.exception.BusinessException;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.entity.InspectionTask;
import com.smartsubstation.mapper.InspectionTaskMapper;
import com.smartsubstation.service.IInspectionTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 巡检任务服务实现
 */
@Slf4j
@Service
public class InspectionTaskServiceImpl extends ServiceImpl<InspectionTaskMapper, InspectionTask>
        implements IInspectionTaskService {

    @Override
    public PageResult<?> pageQuery(PageQuery pageQuery) {
        Page<InspectionTask> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        Page<InspectionTask> result = this.page(page, null);

        return PageResult.<InspectionTask>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .page(result.getCurrent())
                .pageSize(result.getSize())
                .pages(result.getPages())
                .build();
    }

    @Override
    public Object getDetailById(Long taskId) {
        InspectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        return task;
    }

    @Override
    public PageResult<?> getMyTasks(PageQuery pageQuery) {
        // TODO: 实现获取当前用户任务的逻辑
        return pageQuery(pageQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startTask(Long taskId) {
        InspectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        if (!"pending".equals(task.getStatus())) {
            throw new BusinessException("任务状态不正确");
        }

        task.setStatus("in_progress");
        task.setActualStartTime(LocalDateTime.now());
        return this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeTask(Long taskId) {
        InspectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        if (!"in_progress".equals(task.getStatus())) {
            throw new BusinessException("任务状态不正确");
        }

        task.setStatus("completed");
        task.setActualEndTime(LocalDateTime.now());
        task.setProgress(100);
        return this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelTask(Long taskId) {
        InspectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }

        task.setStatus("cancelled");
        return this.updateById(task);
    }

    @Override
    public Long createFromPlan(Long planId) {
        // TODO: 根据计划创建任务
        return null;
    }
}
