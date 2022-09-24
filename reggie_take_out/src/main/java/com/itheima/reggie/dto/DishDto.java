package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("菜品Dto")
public class DishDto extends Dish {
    @ApiModelProperty("菜品口味")
    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();
    @ApiModelProperty(value = "分类的名称", required = true)
    private String categoryName;
    @ApiModelProperty("套餐份数")
    private Integer copies;
}
