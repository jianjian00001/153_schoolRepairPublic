package com.xpu.repair.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.enums.RepairStatusEnum;
import com.xpu.repair.pojo.ExcelData;
import com.xpu.repair.pojo.dto.ResultDTO;
import com.xpu.repair.pojo.entity.Maintenance;
import com.xpu.repair.pojo.entity.Profession;
import com.xpu.repair.pojo.entity.Repair;
import com.xpu.repair.pojo.entity.Technician;
import com.xpu.repair.pojo.vo.MaintenanceVO;
import com.xpu.repair.pojo.vo.UrgentrepairVo;
import com.xpu.repair.service.*;
import com.xpu.repair.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 维修人员表 前端控制器
 * </p>
 *
 * @since 2021-03-29
 */
@Controller
@RequestMapping("/technician")
@Api(value = "维修人员接口")
public class TechnicianController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TechnicianService technicianService;

    @Autowired
    ProfessionService professionService;

    @Autowired
    MaintenanceService maintenanceService;

    @Autowired
    RepairService repairService;

    @Autowired
    FileService fileService;

    @Autowired
    UrgentrepairService urgentrepairService;

    /**
     * 维修人员登录
     *
     * @param id
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "维修人员登录")
    public ResultDTO login(@RequestParam("id") String id, @RequestParam("password") String password, HttpServletRequest request) {
        logger.info("================technician login===================");
        logger.info("admin id: " + id);
        logger.info("admin password: " + password);
        logger.info("================technician login===================");

        if (id != null && password != null) {  //账号密码不为空
            Technician technician = technicianService.getById(id);

            password = DigestUtils.md5DigestAsHex(password.getBytes()); //md5
            if (technician != null && password.equals(technician.getPassword())) { //用户存在，并且密码正确
                request.getSession().setAttribute("technician", technician);
                return ResultDTO.ok().data("url", "technician/index");
            }
        }
        return ResultDTO.error().message("账号或密码错误,请重新登录");
    }

    /**
     * 跳转首页面
     *
     * @return
     */
    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    @ApiOperation(value = "跳转维修人员首页面")
    public String technicianIndex() {
        return "technician/index";
    }

    /**
     * 跳转维修人员个人信息页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/infoPage", method = RequestMethod.GET)
    @ApiOperation(value = "跳转维修人员个人信息页面")
    public String showInfoPage(HttpServletRequest request, Model model) {
        Technician technician = (Technician) request.getSession().getAttribute("technician");
        Integer professionId = technician.getProfessionId();
        //个人工种
        Profession myProfession = professionService.getById(professionId);
        //所有工种
        List<Profession> professionList = professionService.list(null);
        model.addAttribute("myProfession", myProfession);
        model.addAttribute("professionList", professionList);

        return "technician/technicianInfo";
    }

    /**
     * 维修人员更新
     *
     * @param technician
     * @param request
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "维修人员更新")
    public ResultDTO update(Technician technician, HttpServletRequest request) {
        Technician technicianSession = (Technician) request.getSession().getAttribute("technician");
        String technicianSessionId = technicianSession.getId();

        //密码修改时处理
        if (!technicianSession.getPassword().equals(technician.getPassword())){
            technician.setPassword(DigestUtils.md5DigestAsHex(technician.getPassword().getBytes()));
        }
        boolean updateResult = technicianService.updateById(technician.setId(technicianSessionId));
        if (updateResult) {
            return ResultDTO.ok();
        } else {
            return ResultDTO.error();
        }
    }

    /**
     * 跳转到未完成维修页面
     *
     * @param model
     * @param pageNum
     * @param request
     * @return
     */
    @RequestMapping(value = "/showUnCompleteMaintenancePage", method = RequestMethod.GET)
    @ApiOperation(value = "跳转未完成维修的页面")
    public String showUnCompleteMaintenancePage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                                HttpServletRequest request, Model model) {
        Technician technician = (Technician) request.getSession().getAttribute("technician");
        String technicianId = technician.getId();

        Page<MaintenanceVO> maintenanceVOPage = maintenanceService.listUnCompleteMaintenanceByTechnicianId(technicianId, pageNum);
        model.addAttribute("page", maintenanceVOPage);
        return "technician/showUnCompleteMaintenances";
    }

    /**
     * 跳转到维修完成完成页面
     *
     * @param model
     * @param pageNum
     * @param request
     * @return
     */
    @RequestMapping(value = "/showCompleteMaintenancePage")
    @ApiOperation(value = "跳转完成维修页的面")
    public String showCompleteMaintenancePage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                              @RequestParam(value = "startTime", required = false) Date startTime,
                                              @RequestParam(value = "endTime", required = false) Date endTime,
                                              HttpServletRequest request, Model model) {
        //获取维修人员ID
        Technician technician = (Technician) request.getSession().getAttribute("technician");
        String technicianId = technician.getId();

        Page<MaintenanceVO> maintenanceVOPage = maintenanceService.listCompleteMaintenanceByTechnicianId(technicianId, pageNum, startTime, endTime);
        model.addAttribute("page", maintenanceVOPage);
        model.addAttribute("startTime", startTime);
        model.addAttribute("endTime", endTime);

        return "technician/showCompleteMaintenances";
    }

    @RequestMapping(value = "/showReminderPage",method = RequestMethod.GET)
    @ApiOperation(value = "跳转催单记录页面")
    public String showReminderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                   HttpServletRequest request,Model model) {
        Technician technician = (Technician) request.getSession().getAttribute("technician");

        String technicianId = technician.getId();
        Page<UrgentrepairVo> page =  urgentrepairService.listVo(technicianId,null,pageNum);
        model.addAttribute("page",page);
        return "technician/showReminders";
    }

    /**
     * 提交完成维修
     *
     * @param maintenanceId
     * @param file
     * @return
     */
    @RequestMapping(value = "/completeMaintenance", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "提交完成维修")
    public ResultDTO completeMaintenance(@RequestParam(value = "maintenanceId") int maintenanceId,
                                         @RequestParam(value = "file") MultipartFile file) {
        //存储图片到OSS，获取指定url
        String uploadUrl = fileService.upload(file);

        //更新数据库
        //维修表
        Maintenance maintenance = maintenanceService.getById(maintenanceId);
        maintenance.setEndTime(new Date());
        maintenance.setPictureUrl(uploadUrl);
        boolean updateResultMaintenance = maintenanceService.updateById(maintenance);
        //报修表
        Repair repair = repairService.getById(maintenance.getRepairId());
        repair.setStatus(RepairStatusEnum.COMPLETE.getStatusId());
        boolean updateResultRepair = repairService.updateById(repair);
        //返回
        return ResultDTO.ok();
    }

    @RequestMapping(value = "/printCompleteMaintenance",method = RequestMethod.GET)
    @ApiOperation(value = "打印完成的维修记录")
    public void printCompleteMaintenance(@RequestParam(value = "startTime", required = false) Date startTime,
                                              @RequestParam(value = "endTime", required = false) Date endTime,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取维修人员ID
        Technician technician = (Technician) request.getSession().getAttribute("technician");
        String technicianId = technician.getId();

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
}

