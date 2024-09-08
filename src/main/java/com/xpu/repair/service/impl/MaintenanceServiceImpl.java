package com.xpu.repair.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Maintenance;
import com.xpu.repair.mapper.MaintenanceMapper;
import com.xpu.repair.service.MaintenanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xpu.repair.pojo.vo.MaintenanceVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 维修记录表 服务实现类
 * </p>
 *
 * @author MaXinHang
 * @since 2021-03-29
 */
@Service
public class MaintenanceServiceImpl extends ServiceImpl<MaintenanceMapper, Maintenance> implements MaintenanceService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MaintenanceMapper maintenanceMapper;

    private static final int SIZE = 10;

//    @Override
//    public Page<MaintenanceVO> findCompleteMaintenance(int pageNum) {
//        Page<MaintenanceVO> page = new Page<>(pageNum,SIZE);
//        List<MaintenanceVO> successMaintenanceVO = maintenanceMapper.findSuccessMaintenanceVO(page);
//        page.setRecords(successMaintenanceVO);
//        return page;
//    }

    @Override
    public Page<MaintenanceVO> listUnCompleteMaintenanceByTechnicianId(String technicianId, int pageNum) {
        Page<MaintenanceVO> page = new Page<>(pageNum,SIZE);
        List<MaintenanceVO> unCompleteMaintenanceVO = maintenanceMapper.listUnCompleteMaintenanceByTechnicianId(page,technicianId);
        page.setRecords(unCompleteMaintenanceVO);
        logger.info("records {}",unCompleteMaintenanceVO);
        return page;
    }

    @Override
    public Page<MaintenanceVO> listCompleteMaintenanceByTechnicianId(String technicianId, int pageNum, Date startTime,Date endTime) {
        Page<MaintenanceVO> page = new Page<>(pageNum,SIZE);
        List<MaintenanceVO> unCompleteMaintenanceVO = maintenanceMapper.listCompleteMaintenanceByTechnicianId(page,technicianId,startTime,endTime);
        page.setRecords(unCompleteMaintenanceVO);
        logger.info("records {}",unCompleteMaintenanceVO);
        return page;
    }

    @Override
    public List<MaintenanceVO> listCompleteMaintenanceByTechnicianId(String technicianId, Date startTime, Date endTime) {
        List<MaintenanceVO> completeMaintenanceVO = maintenanceMapper.listCompleteMaintenance(technicianId,startTime,endTime);
        return completeMaintenanceVO;
    }

    @Override
    public List<MaintenanceVO> listUnCompleteMaintenanceByUserId(String userId) {
        List<MaintenanceVO> maintenanceVOS = maintenanceMapper.listUnCompleteMaintenanceByUserId(userId);
        return maintenanceVOS;
    }

    @Override
    public List<MaintenanceVO> listCompleteMaintenanceByUserId(String userId) {
        List<MaintenanceVO> maintenanceVOS = maintenanceMapper.listCompleteMaintenanceByUserId(userId);
        return maintenanceVOS;
    }
}
