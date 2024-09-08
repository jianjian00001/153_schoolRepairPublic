package com.xpu.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Repair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpu.repair.pojo.vo.RepairVO;

import java.util.List;


/**
 * <p>
 * 报修单表 服务类
 * </p>
 *
 * @since 2022-03-29
 */
public interface RepairService extends IService<Repair> {
    Page<Repair> findRepairPage(int pageNum);

    Page<Repair> findUnallocatedRepairPage(int pageNum);

    Page<RepairVO> findAllRepairs(int pageNum);

    Page<RepairVO> findRepairByUserId(int pageNum, String userId);

    List<RepairVO> findRepairByUserId(String userId);

    Page<RepairVO> findReminders(int pageNum);

    List<RepairVO> findCompleteRepairByUserId(String userId);

    List<RepairVO> findUnCompleteRepairByUserId(String userId);

    List<RepairVO> findRepairReminderByUserId(String userId);
}

