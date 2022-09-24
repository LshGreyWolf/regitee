package com.itheima.reggie.dto;

import com.itheima.reggie.controller.OrderController;
import com.itheima.reggie.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("订单Dto")
public class OrderDto extends Orders {
//    @ApiModelProperty("用户")
//    private String userName;
}
