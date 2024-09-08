package com.xpu.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Technician;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpu.repair.pojo.vo.TechnicianVO;

/**
 * <p>
 * 维修人员表 服务类
 * </p>
 *
 * @since 2022-03-29
 */
public interface TechnicianService extends IService<Technician> {
    Page<TechnicianVO> findTechnicianPage(int pageNum);
}
