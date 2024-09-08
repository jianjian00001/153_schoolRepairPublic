package com.xpu.repair.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.enums.RepairStatusEnum;
import com.xpu.repair.pojo.dto.ResultDTO;
import com.xpu.repair.pojo.entity.*;
import com.xpu.repair.pojo.vo.UrgentrepairVo;
import com.xpu.repair.service.*;
import com.xpu.repair.pojo.vo.RepairVO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @since 2021-03-29
 */
@Controller
@RequestMapping("/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    RepairService repairService;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    MaintenanceService maintenanceService;

    @Autowired
    TechnicianService technicianService;

    @Autowired
    UrgentrepairService urgentrepairService;

    @Autowired
    FileService fileService;

    /**
     * 用户登录
     * @param id
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO login(@RequestParam("id") String id, @RequestParam("password") String password, HttpServletRequest request){
        logger.info("================user login===================");
        logger.info("admin id: "+id);
        logger.info("admin password: "+password);
        logger.info("================user login===================");

        if (id != null && password != null){  //账号密码不为空
            User user = userService.getById(id);

            password = DigestUtils.md5DigestAsHex(password.getBytes()); //Md5
            if (user != null && password.equals(user.getPassword())){ //用户存在，并且密码正确
                request.getSession().setAttribute("user",user);
                return ResultDTO.ok().data("url","user/index");
            }
        }
        return ResultDTO.error().message("账号或密码错误,请重新登录");
    }

    /**
     * 跳转首页面
     * @return
     */
    @RequestMapping(value = {"/index"},method = RequestMethod.GET)
    public String userIndex(){
        return "user/index";
    }

    /**
     * 跳转到用户个人信息页面
     * @return
     */
    @RequestMapping(value = "/infoPage",method = RequestMethod.GET)
    public String messagePage(){
        return "user/userInfo";
    }

    /**
     * 用户更新
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO update(User user, HttpServletRequest request){
        User userSession = (User) request.getSession().getAttribute("user");
        user.setId(userSession.getId());

        //密码修改时
        if (userSession.getPassword().equals(user.getPassword())){
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        }
        boolean updateResult = userService.updateById(user);
        if (updateResult) {
            return ResultDTO.ok();
        } else {
            return ResultDTO.error();
        }
    }

    /**
     * 报修记录ByUserId
     * @param model
     * @param request
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/repairRecord",method = RequestMethod.GET)
    public String showRepairRecordPage(Model model,HttpServletRequest request, @RequestParam(value = "pageNum",required = false,defaultValue = "1")int pageNum){
        User user = (User) request.getSession().getAttribute("user");
        String userId = user.getId();

        //查询报修单通过user_id
        Page<RepairVO> repairByUserId = repairService.findRepairByUserId(pageNum, userId);

        model.addAttribute("page",repairByUserId);

        return "user/showRepairs";
    }

    /**
     * 跳转到提交报修记录页面
     * @return
     */
    @RequestMapping(value = "/addRepairPage",method = RequestMethod.GET)
    public String showAddRepairPage(){
        return "user/addOrUpdateRepair";
    }

    /**
     * 跳转到催单页面
     * @param model
     * @param pageNum
     * @return
     */
    @RequestMapping(value = "/remindersPage",method = RequestMethod.GET)
    public String showRemindersPage(Model model,@RequestParam(value = "pageNum",required = false,defaultValue = "1")int pageNum){
        Page<RepairVO> reminders = repairService.findReminders(pageNum);

        model.addAttribute("page",reminders);
        return "user/reminder";
    }

    /**
     * 进行催单，未完成
     * @param repairId
     * @param request
     * @return
     */
    @RequestMapping(value = "/reminders",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO reminders(int repairId,HttpServletRequest request){
        Repair repair = repairService.getById(repairId);
        //得到状态
        Integer status = repair.getStatus();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("1369688548@qq.com");
        if (status == RepairStatusEnum.UNALLOCATED.getStatusId()){
            //未分配，给管理员发邮件

            mailMessage.setTo("1369688548@qq.com");
            Urgentrepair urgentrepair = new Urgentrepair();
            User user = (User) request.getSession().getAttribute("user");
            urgentrepair.setStatus(repair.getStatus());
            urgentrepair.setRepairId(repairId);
            urgentrepair.setUserId(user.getId());
            urgentrepair.setCreateTime(new Date());
            urgentrepairService.save(urgentrepair);
        }else {
            //分配未完成，给维修人员发邮件，并产生记录
            QueryWrapper<Maintenance> queryWrapper = new QueryWrapper<>();
            Maintenance maintenance = maintenanceService.getOne(queryWrapper.eq("repair_id", repairId));
            Technician technician = technicianService.getById(maintenance.getTechnicianId());
            //接受者邮箱
//            mailMessage.setTo(technician.getEmail());
            mailMessage.setTo("1369688548@qq.com");

            //获得用户ID
            User user = (User) request.getSession().getAttribute("user");

            //记录入库
            Urgentrepair urgentrepair = new Urgentrepair();
            urgentrepair.setStatus(repair.getStatus());
            urgentrepair.setRepairId(repairId);
            urgentrepair.setUserId(user.getId());
            urgentrepair.setCreateTime(new Date());
            urgentrepairService.save(urgentrepair);
        }
        mailMessage.setSubject(repairId+"号报修催单");
        mailMessage.setText("在"+repair.getPlace()+"发生"+repair.getDetail()+"故障，影响正常生活和学习，请及时维修");
//        javaMailSender.send(mailMessage);
        return ResultDTO.ok().data("url","/user/remindersPage");
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
            boolean removeResult = repairService.removeById(repairId);
            if (removeResult){
                return ResultDTO.ok();
            }else {
                return ResultDTO.error().message("删除失败");
            }
        }

        return ResultDTO.error().message("不能删除已经分配或已经维修完成的报修记录");
    }

    /**
     * 新增或更新报修
     * @param detail
     * @param place
     * @param file
     * @return
     */
    @RequestMapping(value = "/addOrUpdateRepair",method = RequestMethod.POST)
    @ResponseBody
    public ResultDTO addOrUpdateRepair(@RequestParam(value = "id",required = false) String id,@RequestParam(value = "detail") String detail,@RequestParam(value = "place") String place,
                                       MultipartFile file,HttpServletRequest request) throws IOException {
        logger.info("id {}",id);
        logger.info("detail:{}",detail);
        logger.info("place: {}",place);
        logger.info("file: {}",file.getSize());

        if (detail.trim().length() == 0 || place.trim().length() == 0){  //修改前端判断
            return ResultDTO.error().message("详情或地点不能为空");
        }

        String uploadUrl = null;
        //上传图片返回访问url
        if (0 != file.getSize()){
            uploadUrl = fileService.upload(file);
        }

        if (id.trim().length() > 0){ //更新
            Integer idInt = Integer.valueOf(id.trim());

            Repair repair = repairService.getById(idInt);
            repair.setPlace(place);
            repair.setDetail(detail);
            repair.setSubmitTime(new Date());
            if (uploadUrl!=null){
                repair.setPictureUrl(uploadUrl);
            }
            repairService.updateById(repair);  //update
            return ResultDTO.ok().message("更新成功").data("url","/user/repairRecord");
        }else{
            //session中获取用户信息
            User user = (User) request.getSession().getAttribute("user");
            Repair repair = new Repair();
            repair.setUserId(user.getId());
            repair.setDetail(detail);
            repair.setPlace(place);
            repair.setPictureUrl(uploadUrl);
            repair.setSubmitTime(new Date());
             repairService.save(repair);  //add
            return ResultDTO.ok().message("添加成功").data("url","/user/repairRecord");
        }
    }

    /**
     * 编辑报修，跳转到新增或更新页面
     * @param repairId
     * @param model
     * @return
     */
    @RequestMapping(value = "/updateRepairPage",method = RequestMethod.GET)
    public String updateRepairPage(@RequestParam(value = "repairId") int repairId,
                                  Model model){
        Repair repair = repairService.getById(repairId);
        if (repair.getStatus() == RepairStatusEnum.UNALLOCATED.getStatusId()){
            model.addAttribute("repair",repair);
            return "user/addOrUpdateRepair";
        }else {
            return "redirect:/user/repairRecord";
        }

    }

    @RequestMapping(value = "/showRemindersPage",method = RequestMethod.GET)
    @ApiOperation(value = "跳转催单记录页面")
    public String showReminderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                   HttpServletRequest request,Model model) {
        User user = (User) request.getSession().getAttribute("user");
        String userId = user.getId();
        Page<UrgentrepairVo> page =  urgentrepairService.listVo(null,userId,pageNum);
        model.addAttribute("page",page);
        return "user/showReminders";
    }

}


