package com.smartsubstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.SubStationSaveRequest;

/**
 * 变电站服务接口
 */
public interface ISubStationService {

    /**
     * 分页查询变电站
     */
    PageResult<?> pageQuery(PageQuery pageQuery);

    /**
     * 根据ID查询
     */
    Object getById(Long stationId);

    /**
     * 保存变电站（新增或更新）
     */
    Long save(SubStationSaveRequest request);

    /**
     * 删除变电站
     */
    Boolean delete(Long stationId);

    /**
     * 获取所有变电站列表
     */
    java.util.List<?> listAll();
}
