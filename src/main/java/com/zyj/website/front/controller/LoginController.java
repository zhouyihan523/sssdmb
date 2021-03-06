package com.zyj.website.front.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * 示例
 * Created by ZhangYJ on 15/11/18.
 */
@Controller
public class LoginController extends BaseController{
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    //@Autowired
    //JpaRealmRepository jpaRealmRepository;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return "redirect:/index";
        }
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @RequiresGuest
    public String login(Model model, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated() || subject.isRemembered()) {
            return "redirect:/index";
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String loginKaptchaCode = request.getParameter("code");

        Session shiroSession = subject.getSession();
//        Object kaptchaCode = shiroSession.getAttribute("KAPTCHA_SESSION_KEY");
//
//        if (kaptchaCode != null && !StringUtils.equalsIgnoreCase(loginKaptchaCode, kaptchaCode.toString())) {
//            model.addAttribute("message", "验证码错误!");
//            return "/login";
//        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, password, false, request.getRemoteHost());
        try {
            subject.login(token);
            //BIConversion.User user = jpaRealmRepository.findUserByName(username);
            //user.setLastLogin(new Date());
            //user = jpaRealmRepository.mergeUser(user);

            return "redirect:/index";
        } catch (UnknownAccountException uae) {
            model.addAttribute("message", "Unknown User!");
            log.info("Unknown User!");
        } catch (IncorrectCredentialsException ice) {
            model.addAttribute("message", "Incorrect Password!");
            log.info("Incorrect Password!");
        } catch (LockedAccountException lae) {
            model.addAttribute("message", "User Locked!");
            log.info("User Locked!");
        } catch (AuthenticationException ae) {
            model.addAttribute("message", "Authentication Failed!");
            log.info("Authentication Failed!");
        }
        return "login";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "index";
    }
}
