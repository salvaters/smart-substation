package com.smartsubstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartsubstation.entity.SubStation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 变电站Mapper
 */
@Mapper
public interface SubStationMapper extends BaseMapper<SubStation> {
}
