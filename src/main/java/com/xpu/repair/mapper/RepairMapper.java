package com.xpu.repair.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.pojo.entity.Repair;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xpu.repair.pojo.vo.RepairVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 报修单表 Mapper 接口
 * </p>
 *
 * @since 2021-03-29
 */
@Repository
public interface RepairMapper extends BaseMapper<Repair> {
    List<RepairVO> listRepairDetail(@Param("page")Page page);

    List<RepairVO> listRepairDetailByRepair(@Param("page") Page page, @Param("repair") Repair repair);

    List<RepairVO> listReminderDetail(@Param("page")Page page);

    List<RepairVO> findAllRepairs(@Param("page")Page<RepairVO> page);

    List<RepairVO> listRepairByUserIdAndStatus(@Param("userId") String userId,@Param("statusId") int statusId);
}
