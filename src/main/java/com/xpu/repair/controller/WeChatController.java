package com.xpu.repair.controller;

import com.xpu.repair.pojo.dto.ResultDTO;
import com.xpu.repair.pojo.entity.Repair;
import com.xpu.repair.pojo.entity.User;
import com.xpu.repair.pojo.vo.MaintenanceVO;
import com.xpu.repair.pojo.vo.RepairVO;
import com.xpu.repair.service.FileService;
import com.xpu.repair.service.MaintenanceService;
import com.xpu.repair.service.RepairService;
import com.xpu.repair.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/wx")
public class WeChatController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserService userService;

    @Autowired
    RepairService repairService;

    @Autowired
    MaintenanceService maintenanceService;

    @Autowired
    FileService fileService;

    /**
     * 登录
     * @param account
     * @param password
     * @param request
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public ResultDTO login(String account, String password, HttpServletRequest request){

        if (account != null && password != null){  //账号密码不为空
            User user = userService.getById(account);

            password = DigestUtils.md5DigestAsHex(password.getBytes()); //Md5
            if (user != null && password.equals(user.getPassword())){ //用户存在，并且密码正确
                request.getSession().setAttribute("user",user);
                return ResultDTO.ok().data("user",user);
            }
        }
        return ResultDTO.error().message("账号或密码错误,请重新登录");
    }

    /**
     * 个人全部报修单
     * @param request
     * @return
     */
    @RequestMapping(value = "/repair/all",method = RequestMethod.POST)
    public ResultDTO listRepairAll(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String userId = user.getId();

        //查询报修单通过user_id
        List<RepairVO> repairs = repairService.findRepairByUserId(userId);

        return ResultDTO.ok().data("repairs",repairs);
    }

    /**
     * 个人未分配和未完成维修的报修单
     * @param request
     * @return
     */
    @RequestMapping(value = "/repair/reminder")
    public ResultDTO listRepairReminder(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String userId = user.getId();

        //查询报修单通过user_id
        List<RepairVO> repairs = repairService.findRepairReminderByUserId(userId);

        return ResultDTO.ok().data("repairs",repairs);
    }

    /**
     * 完成的维修记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/maintenance/success",method = RequestMethod.POST)
    public ResultDTO listMaintenanceSuccess(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String userId = user.getId();

        //查询维修单通过user_id
        List<MaintenanceVO> maintenanceVOS = maintenanceService.listCompleteMaintenanceByUserId(userId);

        return ResultDTO.ok().data("maintenance",maintenanceVOS);
    }

    /**
     * 未完成的维修记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/maintenance/ing",method = RequestMethod.POST)
    public ResultDTO listMaintenanceIng(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        String userId = user.getId();

        //查询维修单通过user_id
        List<MaintenanceVO> maintenanceVOS = maintenanceService.listUnCompleteMaintenanceByUserId(userId);

        return ResultDTO.ok().data("maintenance",maintenanceVOS);
    }

    /**
     * 照片上传
     * @param file
     * @return
     */
    @RequestMapping(value = "/picture/upload")
    public ResultDTO getPictureUrl(MultipartFile file) {
        String uploadUrl = null;
        //上传图片到阿里云OSS返回访问url
        if (0 != file.getSize()){
            uploadUrl = fileService.upload(file);
        }
        logger.info("file: {}",file);
        return ResultDTO.ok().data("pictureUrl",uploadUrl);
    }

    /**
     * 新增报修记录
     * @param imageUrl
     * @param detail
     * @param place
     * @param request
     * @return
     */
    @RequestMapping(value = "/repair/add",method = RequestMethod.POST)
    public ResultDTO addRepair(@RequestParam(value = "imageUrl") String imageUrl, @RequestParam(value = "detail") String detail, @RequestParam(value = "place") String place,
                               HttpServletRequest request){
        //session中获取用户信息
        User user = (User) request.getSession().getAttribute("user");
        Repair repair = new Repair();
        repair.setUserId(user.getId());
        repair.setDetail(detail);
        repair.setPlace(place);
        repair.setPictureUrl(imageUrl);
        repair.setSubmitTime(new Date());
        repairService.save(repair);  //add
        return ResultDTO.ok();
    }
}
