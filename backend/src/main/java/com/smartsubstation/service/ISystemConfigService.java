package com.smartsubstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;

/**
 * 系统配置服务接口
 */
public interface ISystemConfigService {

    /**
     * 获取配置值
     */
    String getConfig(String configKey);

    /**
     * 更新配置
     */
    Boolean updateConfig(String configKey, String configValue);

    /**
     * 获取所有配置
     */
    java.util.List<?> getAllConfigs();

    /**
     * 按分组获取配置
     */
    java.util.Map<String, String> getConfigsByGroup(String groupName);
}
