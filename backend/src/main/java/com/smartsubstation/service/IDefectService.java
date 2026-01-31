package com.smartsubstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.DefectReportRequest;

/**
 * 缺陷服务接口
 */
public interface IDefectService {

    /**
     * 分页查询缺陷
     */
    PageResult<?> pageQuery(PageQuery pageQuery);

    /**
     * 根据ID查询
     */
    Object getById(Long defectId);

    /**
     * 上报缺陷
     */
    Long report(DefectReportRequest request);

    /**
     * 确认缺陷
     */
    Boolean confirm(Long defectId);

    /**
     * 更新状态
     */
    Boolean updateStatus(Long defectId, String status);

    /**
     * 完成处理
     */
    Boolean complete(Long defectId);
}
