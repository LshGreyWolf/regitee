package com.itheima.reggie.servive;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 根据id删除分类
     * @param ids
     */
    public void remove(Long ids);
}
