package com.xpu.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Profession;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 职业表 服务类
 * </p>
 *
 * @since 2022-03-29
 */
public interface ProfessionService extends IService<Profession> {
    Page<Profession> findProfessionPage(int pageNum);
}
