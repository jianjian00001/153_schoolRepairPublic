package com.xpu.repair.service.impl;

import com.xpu.repair.pojo.entity.Admin;
import com.xpu.repair.mapper.AdminMapper;
import com.xpu.repair.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author MaXinHang
 * @since 2021-03-29
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

}
