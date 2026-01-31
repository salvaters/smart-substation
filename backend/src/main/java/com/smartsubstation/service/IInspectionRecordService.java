package com.smartsubstation.service;

import com.smartsubstation.dto.PageQuery;
import com.smartsubstation.dto.PageResult;
import com.smartsubstation.dto.RecordSubmitRequest;

/**
 * 巡检记录服务接口
 */
public interface IInspectionRecordService {

    /**
     * 提交巡检记录
     */
    Long submit(RecordSubmitRequest request);

    /**
     * 批量提交记录
     */
    java.util.List<Long> batchSubmit(java.util.List<RecordSubmitRequest> requests);

    /**
     * 查询任务的巡检记录
     */
    PageResult<?> getRecordsByTask(Long taskId, PageQuery pageQuery);
}
