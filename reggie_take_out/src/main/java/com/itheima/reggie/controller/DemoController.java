package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.servive.CategoryService;
import com.itheima.reggie.servive.DishService;
import com.itheima.reggie.servive.EmployeeService;
import javafx.geometry.HPos;
import lombok.val;
import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
public class DemoController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

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
        Page<Dish> dishPage = new Page<>();
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish>
                queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name !=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,queryWrapper);
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category !=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
              return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }
}
