package com.xpu.repair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.enums.RepairStatusEnum;
import com.xpu.repair.pojo.entity.Urgentrepair;
import com.xpu.repair.mapper.UrgentrepairMapper;
import com.xpu.repair.pojo.vo.UrgentrepairVo;
import com.xpu.repair.service.UrgentrepairService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 催单表 服务实现类
 * </p>
 *
 * @author MaXinHang
 * @since 2021-03-29
 */
@Service
public class UrgentrepairServiceImpl extends ServiceImpl<UrgentrepairMapper, Urgentrepair> implements UrgentrepairService {

    private static final int SIZE = 10;

    @Autowired
    UrgentrepairMapper urgentrepairMapper;

    @Override
    public Page<UrgentrepairVo> listVo(String technicianId,String userId, int pageNum) {
        Page<UrgentrepairVo> page = new Page<>(pageNum,SIZE);
        List<UrgentrepairVo> urgentrepairVoList = urgentrepairMapper.listVo(page,technicianId,userId);
        for (UrgentrepairVo urgentrepairVo : urgentrepairVoList) {
            urgentrepairVo.setStatus(RepairStatusEnum.getById(urgentrepairVo.getRepair().getStatus()).getStatusName());
        }
        page.setRecords(urgentrepairVoList);
        return page;
    }
}
