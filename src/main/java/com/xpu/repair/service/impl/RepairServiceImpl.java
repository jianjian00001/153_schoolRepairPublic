package com.xpu.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xpu.repair.pojo.entity.Repair;
import com.xpu.repair.enums.RepairStatusEnum;
import com.xpu.repair.mapper.RepairMapper;
import com.xpu.repair.service.RepairService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xpu.repair.pojo.vo.RepairVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 报修单表 服务实现类
 * </p>
 *
 * @author MaXinHang
 * @since 2021-03-29
 */
@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Autowired
    RepairMapper repairMapper;

    private static final int SIZE = 10;

    @Override
    public Page<Repair> findRepairPage(int pageNum) {
        Page<Repair> page = new Page<>(pageNum,SIZE);
        repairMapper.selectPage(page,null);
        return page;
    }

    @Override
    public Page<Repair> findUnallocatedRepairPage(int pageNum) {
        Page<Repair> page = new Page<>(pageNum,SIZE);

        //查询条件
        QueryWrapper<Repair> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", RepairStatusEnum.UNALLOCATED.getStatusId());
        //查询数据库
        repairMapper.selectPage(page,queryWrapper);

        return page;
    }

    @Override
    public Page<RepairVO> findAllRepairs(int pageNum) {
        Page<RepairVO> page = new Page<>(pageNum,SIZE);

        //查询数据库
        List<RepairVO> repairVos = repairMapper.findAllRepairs(page);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }
        page.setRecords(repairVos);

        return page;
    }

    @Override
    public Page<RepairVO> findRepairByUserId(int pageNum, String userId) {
        Page<RepairVO> page = new Page<>(pageNum,SIZE);

        //查询条件
        Repair repair = new Repair();
        repair.setUserId(userId);
        //查询数据库
        List<RepairVO> repairVos = repairMapper.listRepairDetailByRepair(page, repair);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }
        page.setRecords(repairVos);

        return page;
    }

    @Override
    public List<RepairVO> findRepairByUserId(String userId) {
        //查询条件
        Repair repair = new Repair();
        repair.setUserId(userId);
        //查询数据库
        List<RepairVO> repairVos = repairMapper.listRepairDetailByRepair(null, repair);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }

        return repairVos;
    }

    @Override
    public Page<RepairVO> findReminders(int pageNum) {
        Page<RepairVO> page = new Page<>(pageNum,SIZE);

        //查询条件
        //查询数据库
        List<RepairVO> repairVos = repairMapper.listReminderDetail(page);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }
        page.setRecords(repairVos);
        return page;
    }

    @Override
    public List<RepairVO> findCompleteRepairByUserId(String userId) {
        int statusId = RepairStatusEnum.COMPLETE.getStatusId();

        List<RepairVO> repairVos = repairMapper.listRepairByUserIdAndStatus(userId,statusId);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }
        return repairVos;
    }

    @Override
    public List<RepairVO> findUnCompleteRepairByUserId(String userId) {
        int statusId = RepairStatusEnum.ALLOCATED.getStatusId();

        List<RepairVO> repairVos = repairMapper.listRepairByUserIdAndStatus(userId,statusId);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }
        return repairVos;
    }

    @Override
    public List<RepairVO> findRepairReminderByUserId(String userId) {
        int unAllocatedStatusId = RepairStatusEnum.UNALLOCATED.getStatusId();
        int allocatedStatusId = RepairStatusEnum.ALLOCATED.getStatusId();

        List<RepairVO> repairUnAllocatedVos = repairMapper.listRepairByUserIdAndStatus(userId, unAllocatedStatusId);
        List<RepairVO> repairAllocatedVos = repairMapper.listRepairByUserIdAndStatus(userId,allocatedStatusId);

        List<RepairVO> repairVos = Lists.newArrayList(repairAllocatedVos);
        repairVos.addAll(repairUnAllocatedVos);
        for (RepairVO repairVo : repairVos) {
            repairVo.setStatusName(RepairStatusEnum.getById(repairVo.getStatus()).getStatusName());
        }
        return repairVos;
    }


}
