package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.servive.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 手机验证码的短信
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用阿里的短信服务
            //SMSUtils.sendMessage("reggie","","",phone,code);
            //将验证码保存到session中
            session.setAttribute(phone,code);
            return  R.success("手机验证码发送成功");
        }
        return  R.success("手机验证码发送失败");

    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
       log.info(map.toString());
       //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //session获取保存的验证码
        Object sessionCode = session.getAttribute(phone);
        //进行验证码的比对  页面提交的验证码和session保存的验证码
        if (sessionCode!=null &&sessionCode.equals(code)){
            //比对成功，登录成功
            //判断当前手机号是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                //如果是新用户，就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //登录成功将用户的id存到服务端  否则过滤器那边过不去，登陆不成功
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return  R.error("登陆失败");

    }
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        //清理session中保存的当前登录的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }


}
