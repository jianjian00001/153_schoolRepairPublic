package com.xpu.repair.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xpu.repair.enums.RepairStatusEnum;
import com.xpu.repair.pojo.ExcelData;
import com.xpu.repair.pojo.dto.ResultDTO;
import com.xpu.repair.pojo.entity.*;
import com.xpu.repair.pojo.vo.MaintenanceVO;
import com.xpu.repair.pojo.vo.RepairVO;
import com.xpu.repair.pojo.vo.TechnicianVO;
import com.xpu.repair.pojo.vo.UrgentrepairVo;
import com.xpu.repair.service.*;
import com.xpu.repair.util.ExcelUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @since 2021-03-29
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Autowired
    TechnicianService technicianService;

    @Autowired
    ProfessionService professionService;

    @Autowired
    RepairService repairService;

    @Autowired
    MaintenanceService maintenanceService;

    @Autowired
    UrgentrepairService urgentrepairService;
    /**
     * 管理员登录
     * @param id
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO login(@RequestParam("id") String id, @RequestParam("password") String password, HttpServletRequest request){
        logger.info("================admin login===================");
        logger.info("admin id: "+id);
        logger.info("admin password: "+password);
        logger.info("================admin login===================");

        if (id != null && password != null){  //账号密码不为空
            Admin admin = adminService.getById(id);

            password = DigestUtils.md5DigestAsHex(password.getBytes()); //Md5处理
            if (admin != null && password.equals(admin.getPassword())){ //用户存在，并且密码正确
                request.getSession().setAttribute("admin",admin);
                return ResultDTO.ok().data("url","admin/index");
            }
        }
        return ResultDTO.error().message("账号或密码错误,请重新登录");
    }

    /**
     * 跳转首页面
     * @return
     */
    @RequestMapping(value = {"/index"},method = RequestMethod.GET)
    public String adminIndex(){
        return "admin/index";
    }

    /**
     * 跳转管理员个人信息页面
     * @return
     */
    @RequestMapping(value = "/infoPage",method = RequestMethod.GET)
    public String infoPage(Model model){
        return "admin/adminInfo";
    }

    /**
     * 管理员更新
     * @param admin
     * @param request
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO updateAdmin(Admin admin,HttpServletRequest request) {
        Admin adminSession = (Admin) request.getSession().getAttribute("admin");
        admin.setId(adminSession.getId());

        //防止密码没有改变
        if (!adminSession.getPassword().equals(admin.getPassword())){
            admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
        }

        adminService.updateById(admin);
        return ResultDTO.ok();
    }

    /**
     * 跳转分页查询用户页面
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/showUsersPage",method = RequestMethod.GET)
    public String showUsersPage(Model model, @RequestParam(value = "pageNum",required = false,defaultValue = "1") int pageNum){
        Page<User> userByPage = userService.findUserByPage(pageNum);
        model.addAttribute("page",userByPage);
        logger.info("userByPage"+userByPage.getRecords());
        return "admin/showUsers";
    }

    /**
     * 跳转添加用户页面
     */
    @RequestMapping(value = "/addUserPage",method = RequestMethod.GET)
    public String addUserPage() {
        return "admin/addUser";
    }

    /**
     * 跳转分页查询维修人员页面
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/showTechniciansPage",method = RequestMethod.GET)
    public String showTechniciansPage(Model model,@RequestParam(value = "pageNum",required = false,defaultValue = "1") int pageNum){
        Page<TechnicianVO> technicianPage = technicianService.findTechnicianPage(pageNum);
        model.addAttribute("page",technicianPage);
        logger.info("technicianByPage:{}",technicianPage.getRecords());
        return "admin/showTechnicians";
    }

    /**
     * 跳转添加维修人员页面
     */
    @RequestMapping(value = "/addTechnicianPage",method = RequestMethod.GET)
    public String addBookPage(Model model) {
        List<Profession> list = professionService.list(null);
        model.addAttribute("professionList",list);
        return "admin/addTechnician";
    }

    /**
     * 跳转到工种页面，携带信息
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/addProfessionPage",method = RequestMethod.GET)
    public String showAddProfessionPage(Model model, @RequestParam(value = "pageNum",required = false,defaultValue = "1") int pageNum){
        Page<Profession> professionPage = professionService.findProfessionPage(pageNum);
        model.addAttribute("page",professionPage);
        return "admin/addProfession";
    }

    /**
     * 跳转未分配的报修记录页面，分页
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "showUnallocatedRepairsPage",method = RequestMethod.GET)
    public String showUnallocatedRepair(Model model,@RequestParam(value = "pageNum",
            required = false,defaultValue = "1") int pageNum) {
        Page<Repair> UnallocatedRepairPage = repairService.findUnallocatedRepairPage(pageNum);

        //所有维修工人
        List<Profession> professionList = professionService.list(null);
        List<Technician> technicianList = Lists.newArrayList();
        for (Profession profession : professionList) {
            QueryWrapper<Technician> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("profession_id",profession.getId());
            List<Technician> technicians = technicianService.list(queryWrapper);
            for (Technician technician : technicians) {
                technician.setName(profession.getName()+"-"+technician.getName());
            }
            technicianList.addAll(technicians);
        }

        model.addAttribute("technicianList",technicianList);
        model.addAttribute("page",UnallocatedRepairPage);
        return "admin/showUnallocatedRepairs";
    }

    /**
     * 跳转所有报修记录页面，分页
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "showRepairsPage",method = RequestMethod.GET)
    public String showRepairsPage(Model model,@RequestParam(value = "pageNum",
            required = false,defaultValue = "1") int pageNum) {
        Page<RepairVO> repairPage = repairService.findAllRepairs(pageNum);
        model.addAttribute("page",repairPage);
        return "admin/showRepairs";
    }

    /**
     * 跳转完成维修页面
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "showCompleteMaintenancePage")
    public String showCompleteMaintenancePage(Model model,
                                              @RequestParam(value = "technicianId",required = false) String technicianId,
                                              @RequestParam(value = "pageNum", required = false,defaultValue = "1") int pageNum,
                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                              @RequestParam(value = "endTime", required = false) Date endTime
                                             ) {
        logger.info("pageNum:{}",pageNum);
        logger.info("startTime:{}",startTime);
        logger.info("endTime:{}",endTime);
        logger.info("technicianId:{}",technicianId);

        if (technicianId != null && technicianId.trim().length() == 0){
            technicianId = null;
        }
        logger.info("technicianId:{}",technicianId);

        Page<MaintenanceVO> completeMaintenance = maintenanceService.listCompleteMaintenanceByTechnicianId(technicianId,pageNum,startTime,endTime);
        List<Technician> technicianList = technicianService.list(null);
        model.addAttribute("page",completeMaintenance);
        model.addAttribute("technicianList",technicianList);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);
        model.addAttribute("technicianId",technicianId);

        return "admin/showCompleteMaintenances";
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @RequestMapping(value = "/deleteUser",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO deleteUser(String userId){
        //删除没有被外键关联的
        QueryWrapper<Repair> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        int count = repairService.count(queryWrapper);
        if (count == 0){
            boolean removeResult = userService.removeById(userId);
            if (removeResult){
                return ResultDTO.ok();
            }
        }
        return ResultDTO.error().message("该用户有保修记录不能直接删除");
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO addUser(User user){
        User userServiceById = userService.getById(user.getId());
        if (userServiceById != null){
            return ResultDTO.error().message("用户已经存在");
        }

        //md5加密处理
        String passwordMd5 = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(passwordMd5);
        boolean save = userService.save(user);
        if (save){
            return ResultDTO.ok().data("url","/admin/showUsersPage");
        }
        return ResultDTO.error().message("添加用户失败");
    }

    /**
     * 删除维修人员
     * technicianId
     * @param technicianId
     * @return
     */
    @RequestMapping(value = "/deleteTechnician",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO deleteTechnician(String technicianId){
        //删除没有被外键关联的
        QueryWrapper<Maintenance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("technician_id",technicianId);
        int count = maintenanceService.count(queryWrapper);
        if (count == 0){
            boolean removeResult = technicianService.removeById(technicianId);
            if (removeResult){
                return ResultDTO.ok();
            }
        }

        return ResultDTO.error().message("该维修人员有维修记录不能直接删除");
    }

    /**
     * 添加维修人员
     * @param technician
     * @return
     */
    @RequestMapping(value = "/addTechnician",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO addTechnician(Technician technician){
        Technician technicianServiceById = technicianService.getById(technician.getId());
        if (technicianServiceById != null){
            return ResultDTO.error().message("维修人员已经存在");
        }

        //md5加密处理
        String passwordMd5 = DigestUtils.md5DigestAsHex(technician.getPassword().getBytes());
        technician.setPassword(passwordMd5);
        boolean save = technicianService.save(technician);
        if (save){
            return ResultDTO.ok().data("url","/admin/showTechniciansPage");
        }
        return ResultDTO.error().message("添加维修人员失败");
    }

    /**
     * 分配维修人员
     * @param repairId
     * @param technicianId
     * @return
     */
    @Transactional
    @RequestMapping(value = "allocated",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO allocated(int repairId, String technicianId) {
        Repair repair = repairService.getById(repairId);
        repair.setStatus(RepairStatusEnum.ALLOCATED.getStatusId());

        QueryWrapper<Repair> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",repairId);
        boolean updateResult = repairService.update(repair, queryWrapper);

        Maintenance maintenance = new Maintenance();
        maintenance.setRepairId(repairId);
        maintenance.setTechnicianId(technicianId);
        maintenance.setStartTime(new Date());

        boolean saveResult = maintenanceService.save(maintenance);
        if (updateResult && saveResult){
            return ResultDTO.ok().data("url","/admin/showUnallocatedRepairsPage");
        }else {
            return ResultDTO.error().message("分配失败");
        }
    }

    /**
     * 添加工种
     * @param professionName
     * @return
     */
    @RequestMapping(value = "/addProfession",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO addProfession(String professionName){
        QueryWrapper<Profession> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",professionName);
        Profession profession = professionService.getOne(queryWrapper);
        if (profession!=null){
            return ResultDTO.error().message("此工种已经存在");
        }

        boolean saveResult = professionService.save(new Profession().setName(professionName));
        if (saveResult) {
            return ResultDTO.ok().data("url","/admin/addProfessionPage");
        }
        return ResultDTO.error().message("添加失败");
    }

    /**
     * 删除工种，通过professionId
     * @param professionId
     * @return
     */
    @RequestMapping(value = "/deleteProfession",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO deleteProfession(int professionId) {
        QueryWrapper<Technician> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("profession_id",professionId);
        int count = technicianService.count(queryWrapper);

        if (count == 0){
            boolean removeResult = professionService.removeById(professionId);
            if (removeResult) {
                return ResultDTO.ok();
            }
        }
        return ResultDTO.error().message("该工种与维修人员关联不能直接删除");
    }

    /**
     * 删除未分配的报修记录
     * @param repairId
     * @return
     */
    @RequestMapping(value = "/deleteRepair",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO deleteRepair(int repairId) {
        Repair repair = repairService.getById(repairId);

        //只有未分配的可以删除
        if (repair.getStatus() == RepairStatusEnum.UNALLOCATED.getStatusId()){
            QueryWrapper<Urgentrepair> queryWrapper = new QueryWrapper<>(); //催单
            queryWrapper.eq("repair_id",repairId);
            int count = urgentrepairService.count(queryWrapper);

            if (count == 0){
                repairService.removeById(repairId);
                return ResultDTO.ok();
            }else {
                return ResultDTO.error().message("不能删除,该用户对此订单已经进行催单,请尽快进行安排维修人员维修");
            }
        }

        return ResultDTO.error().message("不能删除,已经分配或已经维修完成的报修记录");
    }

    @RequestMapping(value = "/printCompleteMaintenance")
    public void printCompleteMaintenance(@RequestParam(value = "startTime", required = false) Date startTime,
                                         @RequestParam(value = "endTime", required = false) Date endTime,
                                         @RequestParam(value = "technicianId",required = false) String technicianId,
                                         HttpServletResponse response) throws Exception {
        if (technicianId != null && technicianId.trim().length() == 0){
            technicianId = null;
        }

        List<MaintenanceVO> maintenanceVOs = maintenanceService.listCompleteMaintenanceByTechnicianId(technicianId, startTime, endTime);

        //Excel实体类
        ExcelData excelData = new ExcelData();
        //设置sheet名称
        excelData.setName("维修记录");
        //设置表头字段
        List<String> titles = new ArrayList<String>();
        titles.add("报修记录Id");
        titles.add("报修地点");
        titles.add("详情信息");
        titles.add("维修人员");
        titles.add("分配时间");
        titles.add("完工时间");
        excelData.setTitles(titles);
        //加入数据
        List<List<Object>> rows = new ArrayList<List<Object>>();
        for (MaintenanceVO maintenanceVO : maintenanceVOs) {
            List<Object> row = new ArrayList<>();
            row.add(maintenanceVO.getRepair().getId());
            row.add(maintenanceVO.getRepair().getPlace());
            row.add(maintenanceVO.getRepair().getDetail());
            row.add(maintenanceVO.getTechnician().getName());
            row.add(maintenanceVO.getStartTime());
            row.add(maintenanceVO.getEndTime());
            rows.add(row);
        }
        //设置要输出的记录
        excelData.setRows(rows);

        ExcelUtil.exportExcel(response,"维修记录.xlsx",excelData);
    }

    @RequestMapping(value = "/showReminderPage",method = RequestMethod.GET)
    @ApiOperation(value = "跳转催单记录页面")
    public String showReminderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                   HttpServletRequest request,Model model) {
        Page<UrgentrepairVo> page =  urgentrepairService.listVo(null,null,pageNum);
        model.addAttribute("page",page);
        return "admin/showReminders";
    }

    public static void main(String[] args) {
        System.out.println(DigestUtils.md5DigestAsHex("123456".getBytes()));
    }
}

