package com.xpu.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Urgentrepair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpu.repair.pojo.vo.UrgentrepairVo;

/**
 * <p>
 * 催单表 服务类
 * </p>
 *
 * @since 2022-03-29
 */
public interface UrgentrepairService extends IService<Urgentrepair> {

    Page<UrgentrepairVo> listVo(String technicianId,String userId,int pageNum);
}
