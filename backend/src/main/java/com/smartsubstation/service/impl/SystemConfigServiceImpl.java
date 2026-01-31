package com.smartsubstation.service.impl;

import com.smartsubstation.entity.SysConfig;
import com.smartsubstation.mapper.SysConfigMapper;
import com.smartsubstation.service.ISystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements ISystemConfigService {

    private final SysConfigMapper configMapper;

    @Override
    public String getConfig(String configKey) {
        SysConfig config = configMapper.selectOne(
                "config_key = #{configKey} and status = 1"
        );

        if (config != null) {
            return config.getConfigValue();
        }

        // 返回默认值
        return getDefaultConfigValue(configKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConfig(String configKey, String configValue) {
        SysConfig config = configMapper.selectOne(
                "config_key = #{configKey}"
        );

        if (config != null) {
            config.setConfigValue(configValue);
            return configMapper.updateById(config) > 0;
        } else {
            SysConfig newConfig = new SysConfig();
            newConfig.setConfigKey(configKey);
            newConfig.setConfigValue(configValue);
            newConfig.setConfigType("string");
            newConfig.setStatus(1);
            return configMapper.insert(newConfig) > 0;
        }
    }

    @Override
    public List<?> getAllConfigs() {
        return configMapper.selectList(
                "status = 1 order by group_name, config_key"
        );
    }

    @Override
    public Map<String, String> getConfigsByGroup(String groupName) {
        List<SysConfig> configs = configMapper.selectList(
                "group_name = #{groupName} and status = 1"
        );

        Map<String, String> result = new HashMap<>();
        for (SysConfig config : configs) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        return result;
    }

    /**
     * 获取配置默认值
     */
    private String getDefaultConfigValue(String configKey) {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("file.upload.maxSize", "10485760");
        defaults.put("file.upload.allowedTypes", "jpg,jpeg,png,pdf");
        defaults.put("task.auto.create.days", "7");
        defaults.put("token.expire.time", "7200");
        return defaults.getOrDefault(configKey, "");
    }
}
