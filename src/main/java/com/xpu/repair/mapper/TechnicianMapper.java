package com.xpu.repair.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Technician;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xpu.repair.pojo.vo.TechnicianVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 维修人员表 Mapper 接口
 * </p>
 *
 * @since 2021-03-29
 */
@Repository
public interface TechnicianMapper extends BaseMapper<Technician> {
    List<TechnicianVO> findPageTechnicians(Page<TechnicianVO> page);
}
