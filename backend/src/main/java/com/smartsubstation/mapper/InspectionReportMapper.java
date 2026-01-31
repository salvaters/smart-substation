package com.smartsubstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartsubstation.entity.InspectionReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 巡检报告Mapper
 */
@Mapper
public interface InspectionReportMapper extends BaseMapper<InspectionReport> {
}
