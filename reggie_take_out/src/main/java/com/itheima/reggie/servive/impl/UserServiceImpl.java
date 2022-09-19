package com.itheima.reggie.servive.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.servive.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
