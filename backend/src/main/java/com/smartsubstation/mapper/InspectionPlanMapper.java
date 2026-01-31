package com.smartsubstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartsubstation.entity.InspectionPlan;
import org.apache.ibatis.annotations.Mapper;

/**
 * 巡检计划Mapper
 */
@Mapper
public interface InspectionPlanMapper extends BaseMapper<InspectionPlan> {
}
