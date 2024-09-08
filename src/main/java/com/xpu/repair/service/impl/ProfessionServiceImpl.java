package com.xpu.repair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Profession;
import com.xpu.repair.mapper.ProfessionMapper;
import com.xpu.repair.service.ProfessionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 职业表 服务实现类
 * </p>
 *
 * @author MaXinHang
 * @since 2021-03-29
 */
@Service
public class ProfessionServiceImpl extends ServiceImpl<ProfessionMapper, Profession> implements ProfessionService {

    @Autowired
    ProfessionMapper professionMapper;

    private static final int SIZE = 10;

    @Override
    public Page<Profession> findProfessionPage(int pageNum) {
        Page<Profession> page = new Page<>(pageNum,SIZE);
        professionMapper.selectPage(page, null);
        return page;
    }
}
