package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.servive.OrderService;
import com.itheima.reggie.servive.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据{}", orders);
        orderService.submit(orders);
        return null;
    }

    @GetMapping("/page")
    @ApiOperation(value = "订单明细分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true),
            @ApiImplicitParam(name = "number", value = "订单号", required = false)
    })
    public R<Page> page(int page, int pageSize, Long number) {
        Orders orders = new Orders();
        Long currentId = BaseContext.getCurrentId();
        User user = userService.getById(currentId);
        String userName = user.getName();
        orders.setUserName(userName);

        Page<Orders> ordersPage = new Page<>(page, pageSize);
//        Page<OrderDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>()
                .eq(number != null, Orders::getNumber, number)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage, queryWrapper);

//
//        BeanUtils.copyProperties(ordersPage,dtoPage,"records");
//        List<Orders> records = ordersPage.getRecords();
//        List<OrderDto> list = records.stream().map((item)->{
//            OrderDto orderDto = new OrderDto();
//            BeanUtils.copyProperties(item,orderDto);
//            Long userId = item.getUserId();
//            Orders user = orderService.getById(userId);
//            if (user != null){
//                String userName = user.getUserName();
//                orderDto.setUserName(userName);
//            }
//            return orderDto;
//
//        }).collect(Collectors.toList());
//        dtoPage.setRecords(list);
        return R.success(ordersPage);
    }
@GetMapping("/userPage")
    public R<Page>  userPage(int page,int pageSize){
    Page<Orders> ordersPage = new Page<>(page, pageSize);

    LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<Orders>()
            .orderByDesc(Orders::getOrderTime);
    orderService.page(ordersPage, queryWrapper);


        return R.success(ordersPage);
    }

}
