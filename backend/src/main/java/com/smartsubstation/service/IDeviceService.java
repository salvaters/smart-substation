package com.smartsubstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.DeviceSaveRequest;

/**
 * 设备服务接口
 */
public interface IDeviceService {

    /**
     * 分页查询设备
     */
    PageResult<?> pageQuery(PageQuery pageQuery);

    /**
     * 根据ID查询
     */
    Object getById(Long deviceId);

    /**
     * 根据二维码查询
     */
    Object getByQrCode(String qrCode);

    /**
     * 保存设备
     */
    Long save(DeviceSaveRequest request);

    /**
     * 删除设备
     */
    Boolean delete(Long deviceId);

    /**
     * 生成设备二维码
     */
    String generateQrCode(Long deviceId);
}
