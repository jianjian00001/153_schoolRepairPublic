package com.xpu.repair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Technician;
import com.xpu.repair.mapper.TechnicianMapper;
import com.xpu.repair.service.TechnicianService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xpu.repair.pojo.vo.TechnicianVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 维修人员表 服务实现类
 * </p>
 *
 * @author MaXinHang
 * @since 2021-03-29
 */
@Service
public class TechnicianServiceImpl extends ServiceImpl<TechnicianMapper, Technician> implements TechnicianService {

    @Autowired
    TechnicianMapper technicianMapper;

    //分页每页数量
    private static final int SIZE = 10;

    @Override
    public Page<TechnicianVO> findTechnicianPage(int pageNum) {
        Page<TechnicianVO> page = new Page<>(pageNum,SIZE);
        List<TechnicianVO> pageTechnicians = technicianMapper.findPageTechnicians(page);
        page.setRecords(pageTechnicians);
        return page;
    }
}
