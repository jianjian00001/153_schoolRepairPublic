package com.xpu.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Maintenance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpu.repair.pojo.vo.MaintenanceVO;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 维修记录表 服务类
 * </p>
 *
 * @since 2022-03-29
 */
public interface MaintenanceService extends IService<Maintenance> {
//    Page<MaintenanceVO> findCompleteMaintenance(int pageNum);

    Page<MaintenanceVO> listUnCompleteMaintenanceByTechnicianId(String technicianId, int pageNum);

    Page<MaintenanceVO> listCompleteMaintenanceByTechnicianId(String technicianId, int pageNum, Date startTime, Date endTime);

    List<MaintenanceVO> listCompleteMaintenanceByTechnicianId(String technicianId, Date startTime, Date endTime);

    List<MaintenanceVO> listUnCompleteMaintenanceByUserId(String userId);

    List<MaintenanceVO> listCompleteMaintenanceByUserId(String userId);
}
