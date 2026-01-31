package com.smartsubstation.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.common.exception.BusinessException;
import com.smartsubstation.common.result.ResultCode;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.DefectReportRequest;
import com.smartsubstation.entity.DefectInfo;
import com.smartsubstation.mapper.DefectInfoMapper;
import com.smartsubstation.service.IDefectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 缺陷服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefectServiceImpl implements IDefectService {

    private final DefectInfoMapper defectInfoMapper;

    @Override
    public PageResult<?> pageQuery(PageQuery pageQuery) {
        Page<DefectInfo> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        LambdaQueryWrapper<DefectInfo> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(pageQuery.getKeyword())) {
            wrapper.and(w -> w.like(DefectInfo::getDefectCode, pageQuery.getKeyword())
                    .or()
                    .like(DefectInfo::getDefectDescription, pageQuery.getKeyword()));
        }

        wrapper.orderByDesc(DefectInfo::getDiscoverTime);

        Page<DefectInfo> result = defectInfoMapper.selectPage(page, wrapper);

        return PageResult.<DefectInfo>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .page(result.getCurrent())
                .pageSize(result.getSize())
                .pages(result.getPages())
                .build();
    }

    @Override
    public Object getById(Long defectId) {
        DefectInfo defect = defectInfoMapper.selectById(defectId);
        if (defect == null) {
            throw new BusinessException(ResultCode.DEFECT_NOT_FOUND);
        }
        return defect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long report(DefectReportRequest request) {
        DefectInfo defect = new DefectInfo();
        BeanUtils.copyProperties(request, defect);

        defect.setDefectCode("DEF" + System.currentTimeMillis());
        defect.setStatus("pending");
        defect.setDiscoverTime(LocalDateTime.now());

        defectInfoMapper.insert(defect);
        return defect.getDefectId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirm(Long defectId) {
        DefectInfo defect = defectInfoMapper.selectById(defectId);
        if (defect == null) {
            throw new BusinessException(ResultCode.DEFECT_NOT_FOUND);
        }

        defect.setStatus("confirmed");
        return defectInfoMapper.updateById(defect) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStatus(Long defectId, String status) {
        DefectInfo defect = defectInfoMapper.selectById(defectId);
        if (defect == null) {
            throw new BusinessException(ResultCode.DEFECT_NOT_FOUND);
        }

        defect.setStatus(status);
        if ("completed".equals(status)) {
            defect.setActualRepairTime(LocalDateTime.now());
        }

        return defectInfoMapper.updateById(defect) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean complete(Long defectId) {
        return updateStatus(defectId, "completed");
    }
}
