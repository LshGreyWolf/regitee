package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.servive.EmployeeService;
import javafx.geometry.HPos;
import lombok.val;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("")
public class DemoController {
    @Autowired
    private EmployeeService employeeService;

//    @PostMapping("/employee/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        val emp= employeeService.getOne(new LambdaQueryWrapper<Employee>()
                                          .eq(Employee::getUsername, employee.getUsername()));
        if (emp == null){
            return R.error("账号不存在，登陆失败");

        }
        if (!emp.getPassword().equals(password)){
            return R.error("密码不正确，请重新输入密码");
        }
        if (emp.getStatus()==0){
            return R.error("账号被禁用，登陆失败");
        }
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    public R<Page> page(int page,int pageSize,String name){
        final Page<Employee> pageInfo = new Page<>(page,pageSize);
        final LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getUsername,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);

    }
}
