package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    //通过注解获取配置文件的属性值
    @Value("${reggie.path}")
    private String basePath;


    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //方法的参数file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //文件原始名
        String originalFilename = file.getOriginalFilename();
        //后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用uuid重新生成文件名，防止文件名重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建目录
        File dir = new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()) {
            //不存在，创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void downLoad(String name, HttpServletResponse response) {

        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流会回到浏览器，在浏览器上展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
