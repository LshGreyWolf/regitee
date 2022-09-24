package com.itheima.reggie.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishVO {
    @ApiModelProperty("菜品id")
    Long dishId;
    @ApiModelProperty("菜品名称")
    String name;

}
