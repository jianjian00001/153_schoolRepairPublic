package com.xpu.repair.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Maintenance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xpu.repair.pojo.vo.MaintenanceVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 维修记录表 Mapper 接口
 * </p>
 *
 * @since 2021-03-29
 */
@Repository
public interface MaintenanceMapper extends BaseMapper<Maintenance> {
    List<MaintenanceVO> findAllMaintenanceVO(Page page);

//    List<MaintenanceVO> findSuccessMaintenanceVO(Page page);

    List<MaintenanceVO> listUnCompleteMaintenanceByTechnicianId(@Param("page")Page<MaintenanceVO> page, @Param("technicianId")String technicianId);

    List<MaintenanceVO> listCompleteMaintenanceByTechnicianId(@Param("page")Page<MaintenanceVO> page,@Param("technicianId") String technicianId, @Param("startTime") Date startTime,@Param("endTime") Date endTime);

    List<MaintenanceVO> listCompleteMaintenance(@Param("technicianId") String technicianId,@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    List<MaintenanceVO> listUnCompleteMaintenanceByUserId(@Param("userId") String userId);

    List<MaintenanceVO> listCompleteMaintenanceByUserId(@Param("userId")String userId);
}
