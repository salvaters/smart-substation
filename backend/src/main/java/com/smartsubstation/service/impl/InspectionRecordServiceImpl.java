package com.smartsubstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.RecordSubmitRequest;
import com.smartsubstation.entity.InspectionRecord;
import com.smartsubstation.mapper.InspectionRecordMapper;
import com.smartsubstation.service.IInspectionRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 巡检记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionRecordServiceImpl implements IInspectionRecordService {

    private final InspectionRecordMapper recordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(RecordSubmitRequest request) {
        InspectionRecord record = new InspectionRecord();
        record.setRecordCode("REC" + System.currentTimeMillis());
        record.setDeviceId(request.getDeviceId());
        record.setItemId(request.getItemId());
        record.setCheckValue(request.getCheckValue());
        record.setCheckResult(request.getCheckResult());
        record.setDescription(request.getDescription());
        record.setPhotoUrls(request.getPhotoUrls());
        record.setCheckTime(LocalDateTime.now());
        record.setIsOffline(0);

        recordMapper.insert(record);
        return record.getRecordId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchSubmit(List<RecordSubmitRequest> requests) {
        List<Long> ids = new ArrayList<>();
        for (RecordSubmitRequest request : requests) {
            Long id = submit(request);
            ids.add(id);
        }
        return ids;
    }

    @Override
    public PageResult<?> getRecordsByTask(Long taskId, PageQuery pageQuery) {
        Page<InspectionRecord> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionRecord::getTaskId, taskId)
                .orderByDesc(InspectionRecord::getCheckTime);

        Page<InspectionRecord> result = recordMapper.selectPage(page, wrapper);

        return PageResult.<InspectionRecord>builder()
                .records(result.getRecords())
                .total(result.getTotal())
                .page(result.getCurrent())
                .pageSize(result.getSize())
                .pages(result.getPages())
                .build();
    }
}
