package com.smartsubstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartsubstation.entity.InspectionTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 巡检任务Mapper
 */
@Mapper
public interface InspectionTaskMapper extends BaseMapper<InspectionTask> {
}
