package com.smartsubstation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.common.exception.BusinessException;
import com.smartsubstation.common.result.ResultCode;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.SubStationSaveRequest;
import com.smartsubstation.entity.SubStation;
import com.smartsubstation.mapper.SubStationMapper;
import com.smartsubstation.service.ISubStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 变电站服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubStationServiceImpl implements ISubStationService {

    private final SubStationMapper stationMapper;

    @Override
    public PageResult<?> pageQuery(PageQuery pageQuery) {
        Page<SubStation> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        LambdaQueryWrapper<SubStation> wrapper = new LambdaQueryWrapper<>();

        // 搜索条件
        if (StrUtil.isNotBlank(pageQuery.getKeyword())) {
            wrapper.and(w -> w.like(SubStation::getStationCode, pageQuery.getKeyword())
                    .or()
                    .like(SubStation::getStationName, pageQuery.getKeyword()));
        }

        // 排序
        if (StrUtil.isNotBlank(pageQuery.getSortField())) {
            boolean isAsc = "asc".equalsIgnoreCase(pageQuery.getSortOrder());
            wrapper.orderBy(true, isAsc, pageQuery.getSortField());
        } else {
            wrapper.orderByDesc(SubStation::getCreateTime);
        }

        Page<SubStation> result = stationMapper.selectPage(page, wrapper);

        return PageResult.<SubStation>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .page(result.getCurrent())
                .pageSize(result.getSize())
                .pages(result.getPages())
                .build();
    }

    @Override
    public Object getById(Long stationId) {
        SubStation station = stationMapper.selectById(stationId);
        if (station == null) {
            throw new BusinessException(ResultCode.STATION_NOT_FOUND);
        }
        return station;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SubStationSaveRequest request) {
        SubStation station = new SubStation();
        BeanUtils.copyProperties(request, station);

        // 检查编码唯一性
        if (request.getStationId() == null) {
            // 新增
            Long count = stationMapper.selectCount(
                    new LambdaQueryWrapper<SubStation>()
                            .eq(SubStation::getStationCode, request.getStationCode())
            );
            if (count > 0) {
                throw new BusinessException("变电站编码已存在");
            }
        }

        stationMapper.insertOrUpdate(station);
        return station.getStationId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long stationId) {
        int rows = stationMapper.deleteById(stationId);
        return rows > 0;
    }

    @Override
    public java.util.List<?> listAll() {
        return stationMapper.selectList(
                new LambdaQueryWrapper<SubStation>()
                        .eq(SubStation::getStatus, 1)
                        .orderByAsc(SubStation::getStationCode)
        );
    }
}
