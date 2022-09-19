package com.itheima.reggie.servive.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.servive.SetmealDishService;
import com.itheima.reggie.servive.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j

public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息   操作seatmeal
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息 操作setmeal_dish
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐同时删除套餐菜品关系的关联数据
     *
     * @param ids
     */
    @Override
    public void removeDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //查询套餐的状态是否可以删除  select count(*) from setmeal where id in(1,2) and status = 1
        queryWrapper.in(Setmeal::getId,ids );
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0){
            //不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //可删除，先删除套餐表的数据
        this.removeByIds(ids );
        //再删除关联关系
        //delete from setmeal_dish where setmeal_id in (ids)  不能使用removeByIds这个方法，因为这个方法删除的是表的主键id 而这里的
        //ids是setmeal的主键，不是setmeal_dish的主键,所以使用remove方法，根据取出表中的setmeal_id条件再去删除
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids );
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
