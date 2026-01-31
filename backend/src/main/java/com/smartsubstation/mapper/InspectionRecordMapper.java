package com.smartsubstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartsubstation.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 巡检记录Mapper
 */
@Mapper
public interface InspectionRecordMapper extends BaseMapper<InspectionRecord> {
}
