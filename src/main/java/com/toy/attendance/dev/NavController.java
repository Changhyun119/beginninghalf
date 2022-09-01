package com.toy.attendance.dev;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.Api;

@Controller
@Api(tags = "#99.Nav Controller", value = "/", description = "Navigate Static Viewpage")
public class NavController {
    private static Logger LOG = LoggerFactory.getLogger(NavController.class);

    public NavController() {
    }

    // @GetMapping("/login")
    // public String login(HttpServletRequest request, Model model) {
    //     return "/login/index.html";
    // }

    @GetMapping({"/", "/oauth/kakao/callback", "/calendar" , "/main", "/registration", "/system-admin"})
    public ModelAndView root(HttpServletRequest request, Model model) {
        return new ModelAndView("/index.html");
    }

    
}
