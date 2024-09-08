package com.xpu.repair.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@Api(value = "公共接口")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 跳转首页，登录
     *
     * @return
     */
    @ApiOperation(value = "跳转登录页面")
    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    /**
     * 注销
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "注销")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:login";
    }
}
