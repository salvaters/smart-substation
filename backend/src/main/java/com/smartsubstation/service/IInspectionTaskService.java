package com.smartsubstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;

/**
 * 巡检任务服务接口
 */
public interface IInspectionTaskService {

    /**
     * 分页查询任务
     */
    PageResult<?> pageQuery(PageQuery pageQuery);

    /**
     * 根据ID查询任务详情
     */
    Object getDetailById(Long taskId);

    /**
     * 获取我的任务列表
     */
    PageResult<?> getMyTasks(PageQuery pageQuery);

    /**
     * 开始任务
     */
    Boolean startTask(Long taskId);

    /**
     * 完成任务
     */
    Boolean completeTask(Long taskId);

    /**
     * 取消任务
     */
    Boolean cancelTask(Long taskId);

    /**
     * 根据计划创建任务
     */
    Long createFromPlan(Long planId);
}
