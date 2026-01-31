package com.smartsubstation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.common.exception.BusinessException;
import com.smartsubstation.common.result.ResultCode;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.DeviceSaveRequest;
import com.smartsubstation.entity.DeviceInfo;
import com.smartsubstation.mapper.DeviceInfoMapper;
import com.smartsubstation.service.IDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 设备服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements IDeviceService {

    private final DeviceInfoMapper deviceInfoMapper;

    @Override
    public PageResult<?> pageQuery(PageQuery pageQuery) {
        Page<DeviceInfo> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        LambdaQueryWrapper<DeviceInfo> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(pageQuery.getKeyword())) {
            wrapper.and(w -> w.like(DeviceInfo::getDeviceCode, pageQuery.getKeyword())
                    .or()
                    .like(DeviceInfo::getDeviceName, pageQuery.getKeyword()));
        }

        if (StrUtil.isNotBlank(pageQuery.getSortField())) {
            boolean isAsc = "asc".equalsIgnoreCase(pageQuery.getSortOrder());
            wrapper.orderBy(true, isAsc, pageQuery.getSortField());
        } else {
            wrapper.orderByDesc(DeviceInfo::getCreateTime);
        }

        Page<DeviceInfo> result = deviceInfoMapper.selectPage(page, wrapper);

        return PageResult.<DeviceInfo>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .page(result.getCurrent())
                .pageSize(result.getSize())
                .pages(result.getPages())
                .build();
    }

    @Override
    public Object getById(Long deviceId) {
        DeviceInfo device = deviceInfoMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(ResultCode.DEVICE_NOT_FOUND);
        }
        return device;
    }

    @Override
    public Object getByQrCode(String qrCode) {
        DeviceInfo device = deviceInfoMapper.selectOne(
                new LambdaQueryWrapper<DeviceInfo>()
                        .eq(DeviceInfo::getQrCode, qrCode)
        );
        if (device == null) {
            throw new BusinessException("设备不存在或已停用");
        }
        if (device.getStatus() != 1) {
            throw new BusinessException("设备已停用");
        }
        return device;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(DeviceSaveRequest request) {
        DeviceInfo device = new DeviceInfo();
        BeanUtils.copyProperties(request, device);

        // 生成二维码
        if (StrUtil.isBlank(device.getQrCode())) {
            device.setQrCode(generateQrCodeStr());
        }

        // 新增时检查编码唯一性
        if (request.getDeviceId() == null) {
            Long count = deviceInfoMapper.selectCount(
                    new LambdaQueryWrapper<DeviceInfo>()
                            .eq(DeviceInfo::getDeviceCode, request.getDeviceCode())
            );
            if (count > 0) {
                throw new BusinessException("设备编码已存在");
            }
        }

        deviceInfoMapper.insertOrUpdate(device);
        return device.getDeviceId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long deviceId) {
        int rows = deviceInfoMapper.deleteById(deviceId);
        return rows > 0;
    }

    @Override
    public String generateQrCode(Long deviceId) {
        DeviceInfo device = deviceInfoMapper.selectById(deviceId);
        if (device == null) {
            throw new BusinessException(ResultCode.DEVICE_NOT_FOUND);
        }

        // 生成二维码URL（实际应使用二维码生成库）
        String qrCodeUrl = "/api/qrcode/" + device.getQrCode();
        device.setQrCodeUrl(qrCodeUrl);
        deviceInfoMapper.updateById(device);

        return qrCodeUrl;
    }

    /**
     * 生成二维码字符串
     */
    private String generateQrCodeStr() {
        return "DEV" + System.currentTimeMillis();
    }
}
